The following manual patching steps are required:
--Manually install runtime libraries used inside Talend Studio
  1)Go into directory "$TALEND_STUDIO/plugins/org.talend.designer.camel.components.localprovider_6.3.1.20170411_1305-patch"
  2)There you find a ZIP archive named "Talend-Studio-configuration-patch.zip"
  3)Unzip this archive with sub-directories into the studio install directory "$TALEND_STUDIO" (usually "Talend-Studio-20161216_1026-V6.3.1")
--Manually install the patch for the Talend ESB runtime
  1)Go into directory "$TALEND_STUDIO/plugins/org.talend.designer.camel.components.localprovider_6.3.1.20170411_1305-patch"
  2)There you find a ZIP archive named "Talend-ESB-container-patch.zip"
  3)Backup and remove file "$TESB_HOME/container/etc/org.apache.karaf.features.cfg" ("$TESB_HOME" is the Talend ESB instll directory, usually "Talend-ESB-V6.3.1")
  4)Unzip this archive with sub-directories into the Talend ESB install directory "$TESB_HOME" (usually "Talend-ESB-V6.3.1")
