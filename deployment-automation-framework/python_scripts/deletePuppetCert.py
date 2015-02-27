#!/usr/bin/env python
"""Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 
 WSO2 Inc. licenses this file to you under the Apache License,
 Version 2.0 (the "License"); you may not use this file except
 in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.apache.org/licenses/LICENSE-2.0
 
 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied.  See the License for the
 specific language governing permissions and limitations
 under the License.

"""

from subprocess import call
from load_deployment_config import load_server_config

if __name__ == '__main__':
    try:
        print (" Loading server node structure...")
        serverList = load_server_config()
        for vm in serverList:
            print (" Deleting certificate for " + vm + ".openstacklocal...")
            call("puppet cert clean " + vm + ".openstacklocal", shell=True)

    except BaseException as b:
        print 'Exception in deletePuppetCert.py: ', b
