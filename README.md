# image-props-extension
XML Calabash extension step for reading image properties such as width

An XML Calabash extension for extracting image information
from PNG, JPEG, SVG etc. Whereas SVG is currently unsupported
due to an incompatibility between Batik and validator.nu’s
htmlparser.

Written by Oliver Swoboda, le-tex publishing services GmbH
(small portions also by Gerrit Imsieke)

It requires several Apache libraries that are bundled with this repo.

xmlgraphics-commons-1.5.jar
commons-imaging-1.0-SNAPSHOT.jar

For SVG analysis, it needs 

 * batik-1.7/*
 * fop.jar

See http://www.apache.org/licenses/LICENSE-2.0.txt for the libraries'
licenses.

Usage example (from the checked-out directory):

calabash/calabash.sh extensions/transpect/ltx-image-identify/ltx-example.xpl

It requires
[calabash-distro](https://github.com/transpect/calabash-distro) and
[calabash-frontend](https://github.com/transpect/calabash-frontend) to
be installed (for example, by installing calabash-frontend with
recursive submodule checkout).

Step signature:

```xml
<p:declare-step type="tr:image-identify">
  <p:input port="source" primary="true" sequence="true"/>
  <p:output port="result" primary="true" sequence="true"/>
  <p:output port="report" sequence="true"/>
  <p:option name="href"/>
</p:declare-step>
```

tr:image-identify passes documents from its source to its result
port (this is because the step is better chainable this way). As a
side effect, it will create a c:results document with image properties
on the report port. The result (er, report) of the sample invocation
above looks like this:

```
<c:results xmlns:c="http://www.w3.org/ns/xproc-step" name="logo-letex.png">
  <c:result name="mimetype" value="image/png"/>
  <c:result name="formatdescription" value="PNG Portable Network Graphics"/>
  <c:result name="formatdetails" value="Png"/>
  <c:result name="width" value="142px"/>
  <c:result name="height" value="58px"/>
  <c:result name="density" value="-1dpi"/>
  <c:result name="colorspace" value="RGB"/>
  <c:result name="transparency" value="false"/>
  <c:result name="compressionalgorithm" value="PNG Filter"/>
</c:results>
<c:results xmlns:c="http://www.w3.org/ns/xproc-step" name="le-tex_venn.svg">
  <c:result name="mimetype" value="image/svg+xml"/>
  <c:result name="width" value="1000px"/>
  <c:result name="height" value="700px"/>
  <c:result name="density" value="96dpi"/>
</c:results>
```

## Compilation (Unix paths):

```
javac -cp ../../../calabash.jar:xmlgraphics-commons-1.5.jar:commons-imaging-1.0-SNAPSHOT.jar \
  ImageIdentify.java
```
   
### On Cygwin:

```
/cygdrive/c/Program\ Files\ \(x86\)/Java/jdk1.7.0_40/bin/javac \
  -cp $(cygpath -map ../../../calabash.jar:xmlgraphics-commons-1.5.jar:commons-imaging-1.0-SNAPSHOT.jar) \
  ImageIdentify.java
```

(C) 2013--2015, le-tex publising services GmbH.  All rights reserved.
Published under Simplified BSD License:

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

   1. Redistributions of source code must retain the above copyright 
      notice, this list of conditions and the following disclaimer.

   2. Redistributions in binary form must reproduce the above copyright 
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY LE-TEX PUBLISING SERVICES ``AS IS'' AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL LE-TEX PUBLISING SERVICES OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
