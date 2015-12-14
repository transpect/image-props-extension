<p:declare-step 
  xmlns:p="http://www.w3.org/ns/xproc" 
  xmlns:c="http://www.w3.org/ns/xproc-step"
  xmlns:tr="http://transpect.io" 
  xmlns:dbk="http://docbook.org/ns/docbook" 
  name="pipeline" 
  version="1.0">

  <p:option name="image-uri" required="false" select="'logo-letex.png'"/>

  <p:input port="source">
    <p:inline>
      <article xmlns="http://docbook.org/ns/docbook" xmlns:xlink="http://www.w3.org/1999/xlink" version="5.0">
        <title>Images</title>
        <sect1>
          <title>Section1 Title</title>
          <mediaobject>
            <imageobject>
              <imagedata fileref="non-existent.jpg"/>
            </imageobject>
          </mediaobject>
          <mediaobject>
            <imageobject>
              <imagedata fileref="logo-letex.png"/>
            </imageobject>
          </mediaobject>
          <para>Text</para>
        </sect1>
      </article>
    </p:inline>
  </p:input>

  <p:output port="result" primary="true" sequence="true">
    <p:pipe step="image-identify" port="report"/>
  </p:output>

  <p:import href="image-identify-declaration.xpl"/>

  <tr:image-identify name="image-identify">
    <p:with-option name="href" select="(//dbk:imagedata)[2]/@fileref">
      <p:pipe port="source" step="pipeline"/>
    </p:with-option>
  </tr:image-identify>

  <p:sink/>

</p:declare-step>