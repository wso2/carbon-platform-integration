#!/usr/bin/perl
#
# search for a file in all subdirectories
#
require xmlResolver;
package envResolver;

sub readProductList
{
	my $prodCode = $_[0];
	my @productList = split( /,/, $prodCode );
	return @productList;
}

sub resolveProduceName
{
	my $prodName;
	my $prodCode = $_[0];
	if ( $prodCode eq 'AS' )
	{
		$prodName = 'wso2as';
	} elsif ( $prodCode eq 'ESB' )
	{
		$prodName = 'wso2esb';
	} elsif ( $prodCode eq 'GREG' )
	{
		$prodName = 'wso2greg';
	} elsif ( $prodCode eq 'DSS' )
	{
		$prodName = 'wso2dataservices';
	} elsif ( $prodCode eq 'BPS' )
	{
		$prodName = 'wso2bps';
	} elsif ( $prodCode eq 'IS' )
	{
		$prodName = 'wso2is';
	} elsif ( $prodCode eq 'MS' )
	{
		$prodName = 'wso2ms';
	} elsif ( $prodCode eq 'MB' )
	{
		$prodName = 'wso2mb';
	} elsif ( $prodCode eq 'GS' )
	{
		$prodName = 'wso2gs';
	} elsif ( $prodCode eq 'CEP' )
	{
		$prodName = 'wso2cep';
	} elsif ( $prodCode eq 'BRS' )
	{
		$prodName = 'wso2brs';
	}
	return $prodName;
}
1;

sub resolveOffset
{
	my $offset;
	my $prodCode = $_[0];
	if ( $prodCode eq 'AS' )
	{
		$offset = '1';
	} elsif ( $prodCode eq 'ESB' )
	{
		$offset = '2';
	} elsif ( $prodCode eq 'GREG' )
	{
		$offset = '0';
	} elsif ( $prodCode eq 'DSS' )
	{
		$offset = '3';
	} elsif ( $prodCode eq 'BPS' )
	{
		$offset = '4';
	} elsif ( $prodCode eq 'IS' )
	{
		$offset = '5';
	} elsif ( $prodCode eq 'MS' )
	{
		$offset = '6';
	} elsif ( $prodCode eq 'MB' )
	{
		$offset = '7';
	} elsif ( $prodCode eq 'GS' )
	{
		$offset = '8';
	} elsif ( $prodCode eq 'CEP' )
	{
		$offset = '9';
	} elsif ( $prodCode eq 'BRS' )
	{
		$offset = '10';
	}
	return $offset;
}

sub getXmlAttribute
{
	my ($searchit) = @_;
	my $search = '';
	if ( $searchit eq "url" )
	{
		$search = 'Property[@name="url"]';
	}
	if ( $searchit eq "userName" )
	{
		$search = 'Property[@name="userName"]';
	}
	if ( $searchit eq "password" )
	{
		$search = 'Property[@name="password"]';
	}
	if ( $searchit eq "driverName" )
	{
		$search = 'Property[@name="driverName"]';
	}
	if ( $searchit eq "maxActive" )
	{
		$search = 'Property[@name="maxActive"]';
	}
	if ( $searchit eq "maxWait" )
	{
		$search = 'Property[@name="maxWait"]';
	}
	if ( $searchit eq "minIdle" )
	{
		$search = 'Property[@name="minIdle"]';
	}
	return $search;
}
1;

sub resolveXmlName
{
	my $xmlName;
	my $prodCode = $_[0];
	if ( $prodCode eq 'AS' )
	{
		$xmlName = 'product_config/asConfig.xml';
	} elsif ( $prodCode eq 'ESB' )
	{
		$xmlName = 'product_config/esbConfig.xml';
	} elsif ( $prodCode eq 'GREG' )
	{
		$xmlName = 'product_config/gregConfig.xml';
	} elsif ( $prodCode eq 'DSS' )
	{
		$xmlName = 'product_config/dssConfig.xml';
	} elsif ( $prodCode eq 'BPS' )
	{
		$xmlName = 'product_config/bpsConfig.xml';
	} elsif ( $prodCode eq 'IS' )
	{
		$xmlName = 'product_config/isConfig.xml';
	} elsif ( $prodCode eq 'MS' )
	{
		$xmlName = 'product_config/msConfig.xml';
	} elsif ( $prodCode eq 'MB' )
	{
		$xmlName = 'product_config/mbConfig.xml';
	} elsif ( $prodCode eq 'GS' )
	{
		$xmlName = 'product_config/gsConfig.xml';
	} elsif ( $prodCode eq 'CEP' )
	{
		$xmlName = 'product_config/cepConfig.xml';
	} elsif ( $prodCode eq 'BRS' )
	{
		$xmlName = 'product_config/bpsConfig.xml';
	}
	return $xmlName;
}
1;

sub resolveCluster
{
	my $product;
	my $prodCode = $_[0];
	if ( $prodCode eq 'AS' )
	{
		$product = 'appServer';
	} elsif ( $prodCode eq 'ESB' )
	{
		$product = 'esb';
	} elsif ( $prodCode eq 'GREG' )
	{
		$product = 'greg';
	} elsif ( $prodCode eq 'DSS' )
	{
		$product = 'dss';
	} elsif ( $prodCode eq 'BPS' )
	{
		$product = 'bps';
	} elsif ( $prodCode eq 'IS' )
	{
		$product = 'is';
	}
	elsif ( $prodCode eq 'MS' )
	{
		$product = 'ms';
	} elsif ( $prodCode eq 'MB' )
	{
		$product = 'mb';
	} elsif ( $prodCode eq 'GS' )
	{
		$product = 'gs';
	} elsif ( $prodCode eq 'CEP' )
	{
		$product = 'cep';
	} elsif ( $prodCode eq 'BRS' )
	{
		$product = 'brs';
	}
	return $product;
}

sub resolveConfigTag
{
	my $prodCode = $_[0];
	if ( $prodCode eq 'dbconfig' )
	{
	}
}

sub resolveConfigAttribute
{
}

sub resolveConfigPath
{
}
