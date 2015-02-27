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

"""Get_nova_creds function loads nova credentials from OS environment variables

Initialize_cluster function performs OpenStack instance spawning,
  * Load Nova credentials from OS environment variables
  * Nova credentials are located in /root/.bash_profile
  * Spawn instances according to nodes list in deployment.cfg file
  * Assign a floating ip to each instance
  * Get each instance ip and put in to a python dictionary
  * Suspend instances
  * Generate puppet params.pp file from config.pp file with facter updates
  * Resume instances

Terminate_instances function terminate the instances in OpenStack in the order
as per the deployment.cfg [nodes] section
"""

import os
import time
from subprocess import call
from novaclient.v1_1 import client

# Nova credentials are loaded from OS environmental variables
def get_nova_creds():
        return { 'username': os.environ['OS_USERNAME'],
                 'api_key': os.environ['OS_PASSWORD'],
                 'auth_url': os.environ['OS_AUTH_URL'],
                 'project_id': os.environ['OS_TENANT_NAME']
               }

# This will load necessary image, flavor and network information to create
# the given VM instance in OpenStack
def initialize_cluster(serverList, imageName, flavorName, networkName, instancePassword, keyPairName):

	i=0
        instanceList=[]

        # this dictionary contains the IP addresses of the populated instances
        ipmap = {}
	#idmap = {}
        
	# load nova credentials
	creds = get_nova_creds()
        nova = client.Client(**creds)
        image=nova.images.find(name=imageName)

        # Find flavor for 1GB RAM
        flavor=nova.flavors.find(name=flavorName)
        
	# Since multiple networks exist find the relevant network id
        # This enables ssh capabilities to the instance created
        network=nova.networks.find(label=networkName)
        nics = [{'net-id': network.id}]

	# Create OpenStack instances for each server given in nodes.txt file
	for vm in serverList:
                server = nova.servers.create ( name = vm,
					       password = instancePassword,
					       image= image.id,
					       flavor= flavor.id ,
					       key_name = keyPairName,
					       nics =[{'net-id': network.id ,'v4-fixed-ip': ''}])
                instanceList.append(server)
                print server.id
		# This time delay is given to wait till instance's network interface to get
		# up and running for us to collect its ip address 
                time.sleep(15)
                print instanceList[i].status
                
		# Floating ips added to the instances to view the management console of the 
		# given wso2 server
		floating_ip = nova.floating_ips.create(nova.floating_ip_pools.list()[0].name)
                print floating_ip
                server.add_floating_ip(floating_ip)
                
		print instanceList[i].addresses
                ipmap[vm + "-ip"] =  (((instanceList[i].addresses)[networkName])[0])['addr']
                #idmap[vm] =  (((instanceList[i].addresses)[networkName])[0])['addr']
		print ipmap[vm + "-ip"]
                print ipmap
		#print "printing idmap"
		#print idmap
                instanceID=instanceList[i].id
                instanceList[i].suspend()
                #print instanceList[i].status
                i=i+1
        time.sleep(5)
        j=0;
        print ("---- end of instance spawning ----")

	# Fill params.pp file and update members with relevant ip addresses
	# Input: config.pp
	# Output: /etc/puppet/modules/appserver/manifests/params.pp
	infile = open('config.pp')
        outfile = open('/etc/puppet/modules/products/manifests/params.pp', 'w')

        for line in infile:
                for src, target in ipmap.iteritems():
                        line = line.replace(src, target)
                outfile.write(line)
        infile.close()
        outfile.close()

	for vm in serverList:
	        instanceList[j].resume()
        	#time.sleep(10)
        	#instanceList[j].reboot()
		currentNodeIPAddress = ipmap[vm + "-ip"]
		print "Starting server "+vm+"..."
		call("echo -n Please wait...; while ! echo exit | nc "+currentNodeIPAddress+" 9443; do echo -n '.'; sleep 10; done", shell="True")
		print "Server "+vm+" is online on port 9443..."
		# -------------------
		j=j+1

def terminate_instances(serverList):
	creds = get_nova_creds()
        nova = client.Client(**creds)
	for vm in serverList:
		print (" Terminating instance " + vm + "...")
		server = nova.servers.find(name=vm)
		server.delete()

# This block will only get executed when running directly
# This can be used to debug given nova client credentials and authentication
# on a given OpenStack cloud environment
if __name__ == '__main__':
    try:
	print (" ********************************************************")
	print (" ****** Starting stand-alone OpenStack config test ******")
	print (" ********************************************************")
	print ("")
	print (" >>> Starting get_nova_creds check")
	print (" 1. Loading nova credentials...")
	creds = get_nova_creds()
        nova = client.Client(**creds)
	
	print (" 2. Retrieving server list...")
	for server in nova.servers.list():
		print server.id, server.name
	print (" >>> get_nova_creds validated successfully")

	print ("")
	print (" >>> Starting load_nova_configs test")
	print (" Retrieving image, flavor and network information...")

	# Find image= "ubuntu14.04" 
        image=nova.images.find(name="suhan-daf-agentv5-ubuntu14.04")
        print (" nova.images.find: suhan-daf-agentv5-ubuntu14.04 --> " + str(image))

        # Find flavor for 1GB RAM
        flavor=nova.flavors.find(name="m2.small")
        print (" nova.flavors.find: m2.small --> " + str(flavor))

        # Find a given network
        network=nova.networks.find(label="qaa-net")

        # Retrieve the newtork id string from the network object we created
        nics = [{'net-id': network.id}]
        print (" nova.networks.find: qaa-net --> " + str(network))
	print (" >>> load_nova_configs validated successfully")
	print ("")
	print (" ********************************************************")
	print (" ************* OpenStack config test passed *************")
	print (" ********************************************************")

    except BaseException as b:
        print 'Exception in openstack.py: ', b

