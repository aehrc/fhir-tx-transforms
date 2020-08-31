# FHIR Terminology Transforms

A stand-alone command-line tool transfers various clinical terminology and code set resources into FHIR codes systems and concept maps. 

## Build
>mvn clean package

## RUN
>java -jar "target\fhir-tx-transforms-jar-with-dependencies.jar" CONFIG-FILE-PATH

## Configuration file 
Single configuration file for terminology resources transform. 
Each supported resources (list below) has individual controls and configuration.  

FHIR Terminology Server URL
>tx.server.url=TERMINOLOGY-SERVER-URL

Control the server update when execute the parser
>server.update=TRUE/FALSE

Local output folder for FHIR resources
>output.dir=PATH-TO-LOCAL-FOLDER

Control running of individual resource transform
> process.ctv2=TRUE/FALSE

> process.ctv3=TRUE/FALSE

> process.icd=TRUE/FALSE

> process.nicip=TRUE/FALSE

> process.opcs=TRUE/FALSE

> process.ods=TRUE/FALSE

READ V2
>ctv2.coreFile=PATH-TO-LOCAL-FILE

>ctv2.mapFile=PATH-TO-LOCAL-FILE

CTV 3
>ctv3.version.VERSION.folder=PATH-TO-LOCAL-FOLDER

ICD-10-UK
>icd10uk.version.VERSION.codeFile=PPATH-TO-LOCAL-FILE

NICIP
>nicip.version.VERSION.folder=PATH-TO-LOCAL-FOLDER

OPCS-4
>opcs.version.VERSION.codeFile=PATH-TO-LOCAL-FILE

>opcs.version.VERSION.validFile=PATH-TO-LOCAL-FILE

>* Use number only for OPCS version, for example 4.9 will be 49 here.

ODS
>ods.version.VERSION.zipfile=PATH-TO-LOCAL-FILE

Support multiple version

To add multiple version processing for single resource, separate the configuration lines for each version. 
For example. to add 2 versions for CTV 3, the version number is 20160401 and 20170401, use the lines below. 
>ctv3.version.20160401.folder=PATH-TO-LOCAL-FOLDER-20160401

>ctv3.version.20170401.folder=PATH-TO-LOCAL-FOLDER-20170401


## Supported Resources

The FHIR Terminology Transforms project supports the following NHS product to be transformed to FHIR code systems.
- Read Code V2
- Read Code (CTV) V3
- NICIP
- ODS
- OPCS-4
- ICD-10-UK

## Required resources for processing 
The NHS resources are hosted in Technology Reference data Update Distribution(NHS TRUD) web site. The resources files are needed to be prepared before used by NHS Parser. 

### Read code (CTV) V3
 - Resources URL :[https://isd.digital.nhs.uk/trud3/user/authenticated/group/0/pack/9/subpack/19/releases](https://isd.digital.nhs.uk/trud3/user/authenticated/group/0/pack/9/subpack/19/releases)
 - Folder/File: EXTRACTED_FOLDER/V3
 - Transformed Versions: 20170401, 20180702

### Read code V2
 - Resources URL : Need access the retired product from [https://hscic.kahootz.com/connect.ti/t_c_home/grouphome](https://hscic.kahootz.com/connect.ti/t_c_home/grouphome)
 - Folder/File: EXTRACTED_FOLDER/Unified/Corev2.all, EXTRACTED_FOLDER/Unified/Uniicd10.xrf
 - Transformed Versions: 20160401

### NICIP
 - Resources URL :[https://isd.digital.nhs.uk/trud3/user/authenticated/group/0/pack/2/subpack/14/releases](https://isd.digital.nhs.uk/trud3/user/authenticated/group/0/pack/2/subpack/14/releases)
 - Folder/File: EXTRACTED_FOLDER/SUBFOLDER
 - Transformed Versions: 20171001, 20180401,20181001,20190601,20200401

### ODS
 - Resources URL :[https://isd.digital.nhs.uk/trud3/user/authenticated/group/0/pack/5/subpack/341/releases](https://isd.digital.nhs.uk/trud3/user/authenticated/group/0/pack/5/subpack/341/releases)
 - Folder/File: EXTRACTED_FOLDER/fullfile.zip
 - Transformed Versions: 26 monthly release since 20180427

### OPCS-4
 - Resources URL :
[https://isd.digital.nhs.uk/trud3/user/authenticated/group/0/pack/10/subpack/119/releases](https://isd.digital.nhs.uk/trud3/user/authenticated/group/0/pack/10/subpack/119/releases)
 - Folder/File: EXTRACTED_FOLDER/OPCS_VERSION_CodesAndTitles_RELEASE_DATE V1.0.txt,EXTRACTED_FOLDER/OPCS_VERSION _Metadata_RELEASE_DATE V1.0.txt
 - Transformed Versions: 4.8,4.9

### ICD-UK-10
 - Resources URL :
[https://isd.digital.nhs.uk/trud3/user/authenticated/group/0/pack/28/subpack/259/releases](https://isd.digital.nhs.uk/trud3/user/authenticated/group/0/pack/28/subpack/259/releases)
 - Folder/File: EXTRACTED_FOLDER/Content/ICD10_Edition_VERSION_CodesAndTitlesAndMetadata_GB_DATE.txt
 - Transformed Versions: 5th.4th
 
Copyright © 2020, Commonwealth Scientific and Industrial Research Organisation (CSIRO) ABN 41 687 119 230. Licensed under the CSIRO Open Source Software Licence Agreement.
