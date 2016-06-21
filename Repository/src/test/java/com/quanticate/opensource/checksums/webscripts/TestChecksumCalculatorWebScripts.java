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
package com.quanticate.opensource.checksums.webscripts;

import static com.quanticate.opensource.checksums.calculator.TestChecksumCalculatorCore.HASH_MD5;
import static com.quanticate.opensource.checksums.calculator.TestChecksumCalculatorCore.HASH_SHA_1;
import static com.quanticate.opensource.checksums.calculator.TestChecksumCalculatorCore.HASH_SHA_256;
import static com.quanticate.opensource.checksums.calculator.TestChecksumCalculatorCore.HASH_SHA_512;
import static com.quanticate.opensource.checksums.calculator.TestChecksumCalculatorCore.TEST_BYTES;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.transaction.UserTransaction;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.repo.web.scripts.BaseWebScriptTest;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.GUID;
import org.springframework.context.ApplicationContext;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.TestWebScriptServer.GetRequest;
import org.springframework.extensions.webscripts.TestWebScriptServer.Response;

import com.quanticate.opensource.checksums.calculator.ChecksumCalculator;

/**
 * WebScript tests for {@link ChecksumCalculator} (needs Alfresco)
 */
public class TestChecksumCalculatorWebScripts extends BaseWebScriptTest
{
   private AuthenticationComponent authenticationComponent;
   private Repository repositoryHelper;
   private NodeService nodeService;
   private ContentService contentService;
   private TransactionService transactionService;
   private NodeRef companyHome;
   private NodeRef testNode;
   private UserTransaction txn;
   private ChecksumCalculator calculator;

   @Override
   protected void setUp() throws Exception
   {
       super.setUp();

       ApplicationContext ctx = getServer().getApplicationContext();
       this.authenticationComponent = ctx.getBean("authenticationComponent", AuthenticationComponent.class);
       this.repositoryHelper = ctx.getBean("repositoryHelper", Repository.class);
       this.nodeService = ctx.getBean("NodeService", NodeService.class);
       this.contentService = ctx.getBean("ContentService", ContentService.class);
       this.transactionService = ctx.getBean("TransactionService", TransactionService.class);
       this.calculator = ctx.getBean("ChecksumCalculator", ChecksumCalculator.class);

       this.authenticationComponent.setSystemUserAsCurrentUser();

       txn = transactionService.getUserTransaction();
       txn.begin();

       companyHome = this.repositoryHelper.getCompanyHome();
       
       // Create test node
       String name = "HashesTest" + GUID.generate();
       Map<QName, Serializable> props = new HashMap<QName, Serializable>(1);
       props.put(ContentModel.PROP_NAME, name);
       testNode = nodeService.createNode(companyHome, ContentModel.ASSOC_CHILDREN, QName.createQName(name), ContentModel.TYPE_CONTENT, props).getChildRef();
       
       // Put the test data in it
       InputStream is = new ByteArrayInputStream(TEST_BYTES);
       contentService.getWriter(testNode, ContentModel.PROP_CONTENT, false).putContent(is);
   }

   @Override
   protected void tearDown() throws Exception
   {
       txn.rollback();
   }
   
   public void testGetAllowedHashes() throws Exception
   {
      String url = "/com/quanitcate/opensource/checksums/hashes";
      Response resp = sendRequest(new GetRequest(url), Status.STATUS_OK);

      JSONObject json = (JSONObject)new JSONParser().parse(resp.getContentAsString());
      assertNotNull(json);
      assertTrue(json.containsKey("hashes"));
      
      JSONArray hashes = (JSONArray)json.get("hashes");
      assertEquals(calculator.getAllowedHashAlgorithms().size(), hashes.size());
      
      for (int i=0; i<hashes.size(); i++)
      {
         String hash = (String)hashes.get(i);
         assertEquals(true, calculator.getAllowedHashAlgorithms().contains(hash));
      }
   }
   
   public void testGetInvalidHashes() throws Exception
   {
      // TODO Try for invalid nodes
      // TODO Try for unsupported hashes
   }
   
   public void testGetHashes() throws Exception
   {
      String url1 = "/com/quanitcate/opensource/checksums/node/";
      String url2 = "/com/quanitcate/opensource/checksums/noderef/";
      
      // Request one
      // TODO
      
      // Request several
      // TODO
      
      if(1==0){
      assertEquals("", HASH_MD5);
      assertEquals("", HASH_SHA_1);
      assertEquals("", HASH_SHA_256);
      assertEquals("", HASH_SHA_512);
      }
   }
}
