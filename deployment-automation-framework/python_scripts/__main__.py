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

"""This main script is the first script is being invoked for Deployment
Automation. Following tasks are performed,
  * Check which IaaS to be used based on the [environment] env value
    of deployment.cfg (Current implementation is for OpenStack).
  * Get environment specific configuration information.
  * Load cluster node data.
  * Spawn instances in OpenStack.
  * Get facter information from instances and update puppet templates.
TODO 
  * Implement for EC2, Physical machines, Local
  * Evaluate and implement docker based deployment automation
  
"""

# import ec2
from load_deployment_config import get_environment
from load_deployment_config import get_openstack_image
from load_deployment_config import get_openstack_flavor
from load_deployment_config import get_openstack_network
from load_deployment_config import get_openstack_instance_password
from load_deployment_config import get_openstack_key_pair
from load_deployment_config import load_server_config
from openstack import initialize_cluster

if __name__ == '__main__':
    try:
        # get deployment automation environment name
        print (" Finding Environment...")
        environment = get_environment()

        if environment == "openstack":
            """Perform OpenStack specific deployment automation pre-tasks"""

            # load openstack cloud configurations
            print (" Loading nova configuration and spawning instances...")
            imageName = get_openstack_image()
            flavorName = get_openstack_flavor()
            networkName = get_openstack_network()
            instancePassword = get_openstack_instance_password()
            keyPairName = get_openstack_key_pair()

            # load server configurations
            print (" Loading deployment cluster configuration...")
            serverList = load_server_config()

            # spawn cluster topology in OpenStack environment and update puppet templates with instances' facter info
            cluster = initialize_cluster(serverList, imageName, flavorName, networkName, instancePassword, keyPairName)

        else:
            print (" Deployment automation for " + environment + " is not implemented yet...")

    except BaseException as b:
        print 'Exception in __main__.py: ', b
