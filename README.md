# Talend Open Studio for ESB
http://www.talend.com


![alt text](http://www.talend.com/sites/default/files/logo-talend.jpg "Talend")


> Contents

This repository contains the source files for Talend Open Studio for ESB.


## Repository Structure
All Talend Studio repositories follow the same file structure:
```

  |_ main          Main Eclipse plugins and features
    |_ features
    |_ plugins
  |_ test          Eclipse plugins and features for unit tests. 
      |_ features
      |_ plugins
  |_ i18n          Internationalization plugins and features.
      |_ features
      |_ plugins
```

## How to build projects
Use maven to build projects. Go to tesb-tooling-se folder and in 
command line type:
 mvn clean install -Dtycho.targetPlatform=<path_to_tos>

For example,
 mvn clean install -Dtycho.targetPlatform=d:/TOS/TOS_ESB-r77287-V5.1.0NB
 

## Download

You can download this product from the [Talend website](http://www.talend.com/download/esb).


## Usage and Documentation

Documentation is available on [Talend Help Center](http://help.talend.com/).



## Support 

You can ask for help on our [Forum](http://www.talend.com/services/global-technical-support).


## Contributing

We welcome contributions of all kinds from anyone.

Using the bug tracker [Talend bugtracker](http://jira.talendforge.org/) is the best channel for bug reports, feature requests and submitting pull requests.

Feel free to share your Talend components on [Talend Exchange](http://www.talendforge.org/exchange).

## License

Copyright (c) 2006-2014 Talend

Licensed under the LGPLv3 License
