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

import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.test.junitrules.ApplicationContextInit;
import org.alfresco.util.test.junitrules.TemporaryNodes;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * Alfresco-specific tests for {@link ChecksumCalculator} which
 *  require a Repository
 */
public class TestChecksumCalculatorAlfresco 
{
   private static final ApplicationContextInit APP_CONTEXT_INIT = new ApplicationContextInit();
   protected TemporaryNodes testNodes = new TemporaryNodes(APP_CONTEXT_INIT);
   protected NodeRef testContent;
   
   private static ContentService contentService;
   
   @BeforeClass public static void initBasicServices() throws Exception
   {
      contentService = APP_CONTEXT_INIT.getApplicationContext().getBean("ContentService", ContentService.class); 
   }
   @Before public void createTestNode() throws Exception
   {
      AuthenticationUtil.setRunAsUserSystem();

      Repository repositoryHelper = APP_CONTEXT_INIT.getApplicationContext().getBean("repositoryHelper", Repository.class);
      NodeRef companyHome = repositoryHelper.getCompanyHome();
      NodeRef checksumTest = testNodes.createFolder(companyHome, "Checksum Test", "System");

      testContent = testNodes.createNodeWithTextContent(parentNode, nodeCmName, nodeType, nodeCreator, textContent)
   }
   
   // TODO Add remaining tests
}
