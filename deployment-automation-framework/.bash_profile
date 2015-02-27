#!/bin/bash

# With the addition of Keystone, to use an openstack cloud you should
# authenticate against keystone, which returns a **Token** and **Service
# Catalog**.  The catalog contains the endpoint for all services the
# user/tenant has access to - including nova, glance, keystone, swift.
#
# *NOTE*: Using the 2.0 *auth api* does not mean that compute api is 2.0.  We
# will use the 1.1 *compute api*

# Cloud Connection
export OS_AUTH_URL=http://192.168.NN.NN:5000/v2.0
export OS_TENANT_ID=894343243245kj43543j2kuisdads
export OS_TENANT_NAME=“xxx”
export OS_USERNAME=“yyy”
export OS_PASSWORD=“yyy123”



