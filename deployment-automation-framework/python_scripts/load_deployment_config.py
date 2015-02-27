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

"""This script extracts the required configuration information
from the deployment.cfg config file
This is needed prior spawning instances in OpenStack, EC2, etc...

"""
import ConfigParser
import collections

# Global variables
# allow_no_value set to true since the server list may be
# recorded without any value assigned when env=openstack
config = ConfigParser.RawConfigParser(allow_no_value=True)
read = config.read('deployment.cfg')

# Get environment
def get_environment():
	return config.get('environment', 'env')

# Load environment configuration
# OpenStack related configuration parameters
def get_openstack_image():
	return config.get('envconfig', 'image')

def get_openstack_flavor():
	return config.get('envconfig', 'flavor')

def get_openstack_network():
	return config.get('envconfig', 'network')

def get_openstack_instancePassword():
	return config.get('envconfig', 'instancePassword')

def get_openstack_keyPair():
	return config.get('envconfig', 'keyPair')

# Load server list from config file
def load_server_config():
	
	serverList = []
	
	# Put node list in to an ordered dictionary object
	# under section [nodes] in deployment.cfg file
	orderedDic = collections.OrderedDict(config.items('nodes'))
	print orderedDic

	# For each node name append to serverList array
	#for node, ip in orderedDic.iteritems():
    	#	serverList.append(node)

	for node, ip in orderedDic.iteritems():
                nodeValues = node.split(" ")
                print nodeValues #output -> ['elb', '1']
		if len(nodeValues)>1:
			print nodeValues[0] #output -> elb
			serverList.append(nodeValues[0])
			#print serverList[nodeValues[1]]
                        #serverList[nodeValues[0]]= 'true'
                else:
			serverList.append(node)
                        #serverList[nodeValues[0]]= 'false'

	# Return the server list name array
	print serverList
	return serverList

# This block will only get executed when running directly
# This can be used to test config file structure, data retrieval and experimentation
if __name__ == '__main__':
    try:
	serverList = load_server_config()
	print serverList

    except BaseException as b:
        print 'Exception in load_deployment_config: ', b


