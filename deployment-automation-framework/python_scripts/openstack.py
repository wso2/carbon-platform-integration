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
    return {'username': os.environ['OS_USERNAME'],
            'api_key': os.environ['OS_PASSWORD'],
            'auth_url': os.environ['OS_AUTH_URL'],
            'project_id': os.environ['OS_TENANT_NAME']
            }


# This will load necessary image, flavor and network information to create
# the given VM instance in OpenStack
def initialize_cluster(server_list, image_name, flavor_name, network_name, instance_password, key_pair_name):
    i = 0
    instance_list = []

    # this dictionary contains the IP addresses of the populated instances
    ip_map = {}

    # load nova credentials
    nova_credentials = get_nova_creds()
    nova_client = client.Client(**nova_credentials)
    nova_image = nova_client.images.find(name=image_name)

    # Find flavor for 1GB RAM
    nova_flavor = nova_client.flavors.find(name=flavor_name)

    # Since multiple networks exist find the relevant network id
    # This enables ssh capabilities to the instance created
    nova_network = nova_client.networks.find(label=network_name)

    # Create OpenStack instances for each server given in nodes.txt file
    for vm in server_list:
        nova_server = nova_client.servers.create(name=vm,
                                                 password=instance_password,
                                                 image=nova_image.id,
                                                 flavor=nova_flavor.id,
                                                 key_name=key_pair_name,
                                                 nics=[{'net-id': nova_network.id, 'v4-fixed-ip': ''}])
        instance_list.append(nova_server)
        print nova_server.id
        # This time delay is given to wait till instance's network interface to get
        # up and running for us to collect its ip address
        time.sleep(15)
        print instance_list[i].status

        # Floating ips added to the instances to view the management console of the
        # given wso2 server
        floating_ip = nova_client.floating_ips.create(nova_client.floating_ip_pools.list()[0].name)
        print floating_ip
        nova_server.add_floating_ip(floating_ip)

        print instance_list[i].addresses
        ip_map[vm + "-ip"] = ((instance_list[i].addresses[network_name])[0])['addr']
        print ip_map[vm + "-ip"]
        print ip_map

        # We are suspending the instances to freeze the puppet catalog runs. Once we copy the puppet
        # cluster node configuration file we are resuming the instances to resume the catalog runs with
        # new configurations in puppet master.
        instance_list[i].suspend()
        i += 1
    time.sleep(5)
    j = 0
    print ("---- end of instance spawning ----")

    # Fill params.pp file and update members with relevant ip addresses
    # Input: config.pp
    # Output: /etc/puppet/modules/products/manifests/params.pp
    infile = open('config.pp')
    outfile = open('/etc/puppet/modules/products/manifests/params.pp', 'w')

    for line in infile:
        for src, target in ip_map.iteritems():
            line = line.replace(src, target)
        outfile.write(line)
    infile.close()
    outfile.close()

    for vm in server_list:
        instance_list[j].resume()
        current_node_ip_address = ip_map[vm + "-ip"]
        print "Starting server " + vm + "..."

        # Once the instance is resumed we wait till the server fully start on the given port.
        # Then we can resume the next server on queue.
        call(
            "echo -n Please wait...; while ! echo exit | nc " + current_node_ip_address +
            " 9443; do echo -n '.'; sleep 10; done",
            shell=True)
        print "Server " + vm + " is online on port 9443..."
        j += 1


def terminate_instances(server_list):
    nova_credentials = get_nova_creds()
    nova_client = client.Client(**nova_credentials)
    for vm in server_list:
        print (" Terminating instance " + vm + "...")
        nova_server = nova_client.servers.find(name=vm)
        nova_server.delete()

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
        nova_credentials = get_nova_creds()
        nova_client = client.Client(**nova_credentials)

        print (" 2. Retrieving server list...")
        for server in nova_client.servers.list():
            print server.id, server.name
        print (" >>> get_nova_creds validated successfully")

        print ("")
        print (" >>> Starting load_nova_configs test")
        print (" Retrieving image, flavor and network information...")

        # Find image= "ubuntu14.04"
        nova_image = nova_client.images.find(name="daf-agentv5-ubuntu14.04")
        print (" nova.images.find: daf-agentv5-ubuntu14.04 --> " + str(nova_image))

        # Find flavor for 1GB RAM
        nova_flavor = nova_client.flavors.find(name="m2.small")
        print (" nova.flavors.find: m2.small --> " + str(nova_flavor))

        # Find a given network
        nova_network = nova_client.networks.find(label="qaa-net")

        # Retrieve the network id string from the network object we created
        nova_nics = [{'net-id': nova_network.id}]
        print (" nova.networks.find: qaa-net --> " + str(nova_network))
        print (" >>> load_nova_configs validated successfully")
        print ("")
        print (" ********************************************************")
        print (" ************* OpenStack config test passed *************")
        print (" ********************************************************")

    except BaseException as b:
        print 'Exception in openstack.py: ', b

