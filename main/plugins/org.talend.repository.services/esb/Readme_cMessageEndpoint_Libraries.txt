**************************************************
**** Readme - Non UI based installation      *****
**** Talend ESB - cMessageEndpoint Libraries *****
**************************************************

Starting with Talend Studio (Subscription Studio) 6.3.1 we do not ship the RouteBuilder cMessageEndpoint Libraries by default with the Talend Studio anymore.
As the overall size of the libraries reach a critical size of >= 600 MB.  
Beside the installation via the Additional Packages Screen in the Studio there is also a way to install the complete set of libraries in the following way: 
 
Example using a Windows 6.4 (EXE) and Windows path for the P2 Folder:
Talend-Studio-win-x86_64.exe -consoleLog ipse.equinox.p2.director -repository file:///Talend_Full_Studio_p2_repository -installIU org.talend.esb.camel.alldeps.feature.feature.group

The above will install the libraries to the Studio/plugins/org.talend.libraries.camel.alldeps_xxxx folder using the local p2 repository which we ship with all Talend Subscription Products (Talend_Full_Studio_p2_repository-xxx.zip). 
This command allows an offline installation and would not require the 620MB to be downloaded via the internet. 
The -repository parameter above requires a folder and by this you must 'unzip' the Talend_Full_Studio_p2_repository-xxx.zip which comes with the Talend Product to a local folder before you can use the command above.

The above can be used for the Studio but also for the 'CommandLine' as it provides an offline and non-UI based installation of the cMessageEndpoint Libraries.

The Talend Team, 5th Dec 2016