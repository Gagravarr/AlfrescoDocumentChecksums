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

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.apache.commons.codec.binary.Hex;

/**
 * For calculating checksums of Alfresco contents
 */
public class ChecksumCalculator {
   protected static Collection<String> DEFAULT_HASH_ALGOS = 
         Arrays.asList(new String[] { "MD5", "SHA-1", "SHA-256", "SHA-512" });
   protected Collection<String> ALLOWED_HASH_ALGOS = DEFAULT_HASH_ALGOS;
   
   private NodeService nodeService;
   private ContentService contentService;
   public void setNodeService(NodeService nodeService)
   {
      this.nodeService = nodeService;
   }
   public void setContentService(ContentService contentService)
   {
      this.contentService = contentService;
   }

   protected static Collection<String> getDefaultHashAlgorithms()
   {
      return Collections.unmodifiableCollection(DEFAULT_HASH_ALGOS);
   }
   public Collection<String> getAllowedHashAlgorithms()
   {
      return Collections.unmodifiableCollection(ALLOWED_HASH_ALGOS);
   }
   
   public static Map<String,byte[]> getHashes(InputStream data, String...hash)
   {
      MessageDigest[] digests = new MessageDigest[hash.length];
      for (int i=0; i<hash.length; i++)
      {
         String h = hash[i];
         if (! DEFAULT_HASH_ALGOS.contains(h))
         {
            throw new IllegalArgumentException("Unsupported hash '"+h+"'");
         }
         try {
            digests[i] = MessageDigest.getInstance(h);
         } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("Unsupported hash '"+h+"'");
         }
      }
      if (data == null)
      {
         throw new IllegalArgumentException("Stream must be given to hash");
      }
      
      int read = -1;
      byte[] buffer = new byte[8192];
      try {
         while ((read = data.read(buffer)) > -1) {
            for (MessageDigest d : digests) {
               d.update(buffer, 0, read);
            }
         }
      } catch (IOException e) {
         throw new AlfrescoRuntimeException("IO Exception calculating hashes", e);
      }
      
      Map<String,byte[]> hashes = new HashMap<>();
      for (MessageDigest d : digests) {
         hashes.put(d.getAlgorithm(), d.digest());
      }
      return hashes;
   }
   public static Map<String,String> asHex(Map<String,byte[]> hashes)
   {
      // As hex, using Commons Codec
      Map<String,String> hexes = new HashMap<>(hashes.size());
      for (String algo : hashes.keySet()) 
      {
         hexes.put(algo, Hex.encodeHexString(hashes.get(algo)));
      }
      return hexes;
   }
   public static Map<String,String> getHashesHex(InputStream data, String...hash)
   {
      return asHex( getHashes(data, hash) );
   }
   
   public Map<String,byte[]> getContentHashes(NodeRef node, String...hash)
   {
      if (! nodeService.exists(node))
      {
         throw new InvalidNodeRefException(node);
      }
      
      ContentReader reader = contentService.getReader(node, ContentModel.PROP_CONTENT);
      if (reader == null)
      {
         throw new InvalidNodeRefException("No content on node", node);
      }

      try
      {
         InputStream stream = reader.getContentInputStream();
         Map<String,byte[]> hashes = getHashes(stream, hash);
         stream.close();
         
         return hashes;
      }
      catch (IOException e)
      {
         throw new AlfrescoRuntimeException("IO Exception calculating hashes", e);
      }
   }
   public Map<String,String> getContentHashesHex(NodeRef node, String...hash)
   {
      return asHex( getContentHashes(node, hash) );
   }
}
