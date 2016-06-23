/* ====================================================================
  Copyright 2016 Quanticate Ltd

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
==================================================================== */
package com.quanticate.opensource.checksums.calculator;

import static com.quanticate.opensource.checksums.calculator.TestChecksumCalculatorCore.HASH_MD5;
import static com.quanticate.opensource.checksums.calculator.TestChecksumCalculatorCore.HASH_SHA_1;
import static com.quanticate.opensource.checksums.calculator.TestChecksumCalculatorCore.HASH_SHA_256;
import static com.quanticate.opensource.checksums.calculator.TestChecksumCalculatorCore.HASH_SHA_512;
import static com.quanticate.opensource.checksums.calculator.TestChecksumCalculatorCore.TEST_TEXT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.GUID;
import org.alfresco.util.test.junitrules.ApplicationContextInit;
import org.alfresco.util.test.junitrules.TemporaryNodes;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Alfresco-specific tests for {@link ChecksumCalculator} which
 *  require a Repository
 */
public class TestChecksumCalculatorAlfresco 
{
   private static final ApplicationContextInit APP_CONTEXT_INIT = new ApplicationContextInit();
   protected TemporaryNodes testNodes = new TemporaryNodes(APP_CONTEXT_INIT);
   protected NodeRef testContent;
   
   protected static ChecksumCalculator calculator;
   
   @BeforeClass public static void init()
   {
      calculator = APP_CONTEXT_INIT.getApplicationContext().getBean("ChecksumCalculator", ChecksumCalculator.class);
   }
   
   @Before public void createTestNode() throws Exception
   {
      AuthenticationUtil.setRunAsUserSystem();

      Repository repositoryHelper = APP_CONTEXT_INIT.getApplicationContext().getBean("repositoryHelper", Repository.class);
      NodeRef companyHome = repositoryHelper.getCompanyHome();
      String testNodeName = "Checksum Test " + GUID.generate();
      NodeRef checksumTest = testNodes.createFolder(companyHome, testNodeName, "Admin");

      testContent = testNodes.createNodeWithTextContent(checksumTest, "Test.txt", 
                                      ContentModel.TYPE_CONTENT, "Admin", TEST_TEXT);
   }
      
   @Test
   public void testInvalidHashes()
   {
      try
      {
         calculator.getContentHashes(null, "MD5");
         fail("Null not valid");
      } 
      catch (IllegalArgumentException e) {} 
      try
      {
         calculator.getContentHashesHex(null, "MD5");
         fail("Null not valid");
      } 
      catch (IllegalArgumentException e) {}
      
      try
      {
         calculator.getContentHashes(testContent, "Invalid");
         fail("Invalid not valid");
      } 
      catch (IllegalArgumentException e) {} 
      try
      {
         calculator.getContentHashes(testContent, "MD5", "Invalid");
         fail("Invalid not valid");
      } 
      catch (IllegalArgumentException e) {} 
   }
   
   @Test
   public void testHexHashes()
   {
      Map<String,String> hashes;
      
      hashes = calculator.getContentHashesHex(testContent, "MD5");
      assertEquals(1, hashes.size());
      assertEquals(HASH_MD5, hashes.get("MD5"));
      
      hashes = calculator.getContentHashesHex(testContent, "MD5", "MD5");
      assertEquals(1, hashes.size());
      assertEquals(HASH_MD5, hashes.get("MD5"));
      
      hashes = calculator.getContentHashesHex(testContent, "MD5", "SHA-1");
      assertEquals(2, hashes.size());
      assertEquals(HASH_MD5, hashes.get("MD5"));
      assertEquals(HASH_SHA_1, hashes.get("SHA-1"));
      
      hashes = calculator.getContentHashesHex(testContent, "SHA-512", "SHA-256", "SHA-1");
      assertEquals(3, hashes.size());
      assertEquals(HASH_SHA_1, hashes.get("SHA-1"));
      assertEquals(HASH_SHA_256, hashes.get("SHA-256"));
      assertEquals(HASH_SHA_512, hashes.get("SHA-512"));
   }
}
