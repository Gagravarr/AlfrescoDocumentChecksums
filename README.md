Alfresco Document Checksums
===========================

This is an extension to Alfresco (both Repository and Share), which allows
for the display of Checksums of your documents within Share. It supports
optional pre-computation of checksums on the repository side, along with
on-the-fly checksum generation as required. Within Share, the checksums
are displayed on a new page, and there's control of what kinds of documents
that's shown for.

This builds on existing Alfresco Checksum ideas, such as
http://blog.productivist.com/generate-checksums-for-alfresco-content/ and
http://subversion.assembla.com/svn/seedhealth/trunk/health_behavior/src/au/gov/nehta/action/ChecksumActionExecutor.java
but tries to be more general, and doesn't require pre-computation.

Share - TODO
============
Provide a page in Share to show the checksums

WebScripts - TODO
=================
Finish and document the webscripts here

Installation - TODO
===================
Package as two AMPs

Building - TODO
===============
TODO Convert this to be buildable by the SDK

License
=======
The code is available under the Apache License version 2. However, it builds
on top of Alfresco, which is under the LGPL v3 license, so in most cases
the resulting system will fall under the stricter LGPL rules...
