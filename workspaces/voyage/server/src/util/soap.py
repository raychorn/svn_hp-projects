#####################################################
#
# soap.py
#
# Copyright 2009 Hewlett-Packard Development Company, L.P.
#
# Hewlett-Packard and the Hewlett-Packard logo are trademarks of
# Hewlett-Packard Development Company, L.P. in the U.S. and/or other countries.
#
# Confidential computer software. Valid license from Hewlett-Packard required
# for possession, use or copying. Consistent with FAR 12.211 and 12.212,
# Commercial Computer Software, Computer Software Documentation, and Technical
# Data for Commercial Items are licensed to the U.S. Government under
# vendor's standard commercial license.
#
# Author:
#    Mohammed M. Islam
#    Modified from Chris Frantz's original
# 
# Description:
#    Do some one-time initialization for the suds soap library
#
#####################################################
from util.resource import resource
from suds.xsd.sxbasic import Import as XSDImport

######################################################################
#
# Statically bind the imported XML schemas to local files.
#
# Note: You must also change the <import> tags in the schemas to not
# have a schemaLocation attribute or the suds library will use the
# schemaLocation instead of the static binding.
#
######################################################################
def f(fn):
    return 'file://' + resource('external/' + fn)

def setup():
    XSDImport.bind('http://www.w3.org/XML/1998/namespace', f('xml.xsd'))
    XSDImport.bind('http://www.w3.org/2001/XMLSchema', f('XMLSchema.xsd'))
    XSDImport.bind('http://www.w3.org/2000/09/xmldsig#', f('xmldsig-core-schema.xsd'))
    XSDImport.bind('http://schemas.xmlsoap.org/soap/encoding/', f('soap-encoding-1.1.xsd'))
    XSDImport.bind('SOAP-ENC', f('soap-encoding.xsd'))
    XSDImport.bind('wsu', f('wsu.xsd'))
    XSDImport.bind('wsse', f('wsse.xsd'))
    XSDImport.bind('http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd', f('wsu.xsd'))

# vim: ts=4 sts=4 sw=4:
