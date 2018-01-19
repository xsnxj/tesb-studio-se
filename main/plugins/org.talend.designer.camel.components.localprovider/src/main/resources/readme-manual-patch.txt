The following manual patching steps are required for the patched cMQConnectionFactory component to run
properly:

-- Manually install runtime libraries used inside Talend Studio (may not be required if Talend Studio has
   online access)
   1) Go into directory 
      "$TALEND_STUDIO/plugins/org.talend.designer.camel.components.localprovider_6.4.1.xxxxxxxx_xxxx-patch"
   2) There you find a ZIP archive named "Talend-Studio-configuration-patch.zip"
   3) Unzip this archive with sub-directories into the studio install directory "$TALEND_STUDIO"
      ("Talend-Studio-xxxxxxxx_xxxx-V6.4.1")
