The following manual patching steps are required for the patched cMQConnectionFactory component to run
properly:

-- Manually install runtime libraries used inside Talend Studio (may not be required if Talend Studio has
   online access)
   1) Go into directory 
      "$TALEND_STUDIO/plugins/org.talend.designer.camel.components.localprovider_6.4.1.xxxxxxxx_xxxx-patch"
   2) There you find a ZIP archive named "Talend-Studio-configuration-patch.zip"
   3) Unzip this archive with sub-directories into the studio install directory "$TALEND_STUDIO"
      ("Talend-Studio-xxxxxxxx_xxxx-V6.4.1")

The following manual patching steps are required for services which are configured to require authentication
to run properly with the patched blueprint template:

-- Manually install the patch for the Talend ESB runtime
   1) Go into directory
      "$TALEND_STUDIO/plugins/org.talend.designer.camel.components.localprovider_6.4.1.xxxxxxxx_xxxx-patch"
   2) There you find a ZIP archive named "Talend-ESB-container-patch.zip"
   3) Backup and remove file "org.talend.esb.job.controller-6.4.1-org.talend.esb.job.service.cfg" in
      directory "$TESB_HOME/container/system/org/talend/esb/job/org.talend.esb.job.controller/6.4.1/"
      ("$TESB_HOME" is the Talend ESB instll directory, usually "Talend-ESB-V6.4.1").
      If there is a file "$TESB_HOME/container/etc/org.talend.esb.job.service.cfg", remove it also
   4) Unzip this archive with sub-directories into the Talend ESB install directory "$TESB_HOME"
      (usually "Talend-ESB-V6.4.1")
