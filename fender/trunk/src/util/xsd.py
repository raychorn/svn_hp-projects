######################################################################
#
# Copyright 2009 Chris Frantz
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the (LGPL) GNU Lesser General Public License as
# published by the Free Software Foundation; either version 2 of the 
# License, or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Library Lesser General Public License for more details at
# ( http://www.gnu.org/licenses/lgpl.html ).
#
######################################################################
from suds import TypeNotFound
from suds.sax.parser import Parser
from suds.xsd import qualify
from suds.xsd.schema import Schema, SchemaCollection
from suds.xsd.query import TypeQuery
from suds.resolver import Resolver
from suds.builder import Builder
from suds.options import Options
from suds.mx import Content
from suds.mx.literal import Literal
from suds.umx.typed import Typed
from suds.transport.https import HttpAuthenticated
from suds.sudsobject import Object as SudsObject
from suds.sudsobject import Factory as InstFactory
#import json

from logging import getLogger
import os.path

log = getLogger(__name__)

# Dynamically append the 'add_schema' method to the SchemaCollection
# class.  This method is exactly the same as 'add' in newer versions
# of the SUDS library.
def add_schema(self, schema):
    key = schema.tns[1]
    existing = self.namespaces.get(key)
    if existing is None:
        self.children.append(schema)
        self.namespaces[key] = schema
    else:
        existing.root.children += schema.root.children
        existing.root.nsprefixes.update(schema.root.nsprefixes)
SchemaCollection.add_schema = add_schema

_options = Options()
_options.transport = HttpAuthenticated()
class FakeWSDL:
    options = _options
    url = None



class helper(SudsObject, object):
    '''
    A helper class for loading and saving Suds Objects according to
    a schema.  Do not use this class directly:  Create or derive
    a class from a schema defined datatype to get a class which will
    contain this class as part of the inheritance heirarchy.
    '''
    def load(self, filename=None, xml=None):
        '''
        Load object content from an XML file

        @type filename: string
        @param filename: Optional file to load.  The default filename
                         is self.filename.
        '''
        if not filename:
            filename = self.filename

        # Read the xml data file
        if not xml:
            sax = Parser()
            try:
                xml = sax.parse(file=filename).root()
            except Exception, e:
                log.debug("Error reading '%s'.  Using defaults.\n%s", filename, e)
                return

        # Compute the name of the XML tag
        if self.__namespace__:
            name = ':'.join((self.__namespace__, self.__basename__))
        else:
            name = self.__basename__

        # Query the schema for the data type
        schema = self.__builder__.schema
        qref = qualify(name, schema.root)
        query = TypeQuery(qref)
        t = query.execute(schema)

        # Create a schema-aware unmarshaller and process the
        # XML
        umx = Typed(schema)
        obj = umx.process(xml, t)

        # Update ourself with the new data
        # Unfreeze the object so the current __keylist__ and the
        # __keylist__ in the loaded object get merged.
        self.__frozen__ = False
        self._update(self, obj)
        #for (k, v) in obj:
        #    # Don't update empty items
        #    if v:
        #        setattr(self, k, v)
        self.__frozen__ = True

    @staticmethod
    def _update(mine, theirs):
        for k, v in theirs:
            # Traverse the object tree
            if isinstance(v, SudsObject):
                if k in mine:
                    helper._update(mine[k], v)
                else:
                    mine[k] = v
            elif v == '':
                # Suds decodes <tag></tag> into an empty string even
                # if the schema says it's an object.
                # If 'mine' has an object at this node, we want to consider
                # the empty string to be an empty object and not update 'mine'.
                # However, if 'mine' has a simple type at this node, then
                # we do want to update.
                if not isinstance(getattr(mine, k, None), SudsObject):
                    mine[k] = v
            elif v is not None:
                # Don't update empty items
                mine[k] = v

    def xml(self):
        '''
        Return object content as XML.

        @return: xml data
        '''
        # Create a schema aware marshaller and a Content object
        # and return an XML object
        mx = Literal(self.__builder__.schema)
        content = Content(self.__basename__, self)
        return mx.process(content)


    def save(self, filename=None):
        '''
        Save object content to an XML file

        @type filename: string
        @param filename: Optional file to save.  The default filename
                         is self.filename.
        '''
        if not filename:
            filename = self.filename

        # Write the data to the file as xml
        f = open(filename, 'w')
        f.write(str(self.xml()))
        f.close()

    def __setattr__(self, name, value):
        '''
        Overrides sudsobject's __setattr__ to account for a
        __frozen__ object.  When an object is __frozen__, the
        __keylist__ won't get modified when new instance variables
        are added.

        __frozen__ is the normal behavior for objects subclassed
        from an XML schema via the xmlObject function.
        '''
        if not self.__frozen__:
            SudsObject.__setattr__(self, name, value)
        else:
            self.__dict__[name] = value

    def __delattr__(self, name):
        '''
        Overrides sudsobject's __delattr__ to account for a
        __frozen__ object.  When an object is __frozen__, the
        __keylist__ won't get modified when new instance variables
        are deleted.
        '''
        try:
            SudsObject.__delattr__(self, name)
        except Exception, ex:
            if not self.__frozen__:
                raise ex

    def default(self, unfreeze=True, **kwargs):
        '''
        Set default attributes in an object, optionally unfreezing the
        object when setting those attributes.

        If you unfreeze (the default), attributes will get added to
        __keylist__ and will therefore be serialized to XML or JSON
        when the object is saved.

        If you do not unfreeze the object, attributes will not get added
        to __keylist__ and will be hidden from serialization.  You can use
        this to set defaults that you don't necessarily want saved.

        @type unfreeze: boolean
        @param unfreeze: Whether or not to unfreeze the object before
            setting/adding attributes.
        @type kwargs: keyword arguments
        @param kwargs: A list of attributes and their default values.
        '''

        frozen = self.__frozen__
        if (unfreeze):
            self.__frozen__ = False

        if kwargs:
            for k,v in kwargs.items():
                self.__setattr__(k, v)
        else:
            empty = self.__builder__.build(self.__basename__)
            for k in empty.__keylist__:
                self.__setattr__(k, empty[k])

        self.__frozen__ = frozen

class builder:
    def __init__(self, xml=None, schema=None, otherschemas=[]):
        self.xml = xml
        # Create a SchemaCollection and then immediately set its wsdl
        # to None (it isn't needed)
        self.container = SchemaCollection(FakeWSDL)
        self.container.wsdl = None

        # Call merge to Build the schema and put it in the container
        # and then load everything
        self.merge(xml, schema, otherschemas)

        # Build the other junk we'll need
        self.resolver = Resolver(self.schema)
        self.builder = Builder(self.resolver)
        self.namespace = 'tns'
        self.cache = dict()

    def merge(self, xml=None, schema=None, otherschemas=[]):
        if xml:
            schema = Schema(xml, None, _options, container=self.container)

        self.container.add_schema(schema)
        for s in otherschemas:
            self.container.add_schema(s)
        # suds-0.3.7 self.container.load()
        # suds-0.3.9 self.schema = self.container.load(_options)
        self.schema = self.container.load()

    def build(self, name, namespace=None):
        '''
        Build a data type defined by this object's XML schema

        @type name: string
        @param name: The schema-defined type to build
        @type namespace: namespace
        @param namespace: An optional namespace.  The default namespace is
                     the value of self.namespace.
        @rtype: object
        @return: A new instance of the requsted object type.
        '''
        if not namespace:
            namespace=self.namespace
        if namespace:
            name = ':'.join((namespace, name))
        mytype = self.resolver.find(name)
        if mytype  is None:
            raise TypeNotFound(name)
        if mytype.enum():
            ret = InstFactory.object(name)
            for (e, a) in mytype.children():
                i = e.name.replace(' ', '_').replace('-', '_').upper()
                setattr(ret, i, e.name)
        else:
            ret = self.builder.build(mytype)
        return ret

    def new(self, name):
        '''
        Return a new classobject for the named type

        @type name: string
        @param name: The name of the class to build
        @rtype: classobject
        @return: The class object for the named class
        '''
        cls = self.cache.get(name, None)
        if not cls:
            t = self.resolver.find(name)
            ivals = dict(
                    filename=None,
                    __frozen__=True,
                    __builder__=self,
                    __basename__=t.name,
                    __namespace__=self.namespace
            )
            cls = type(str(t.name), (helper,), ivals)
            self.cache[name] = cls
        return cls

class wrapper:
    def __init__(self, builder):
        self.__builder__ = builder
    @property
    def __schema__(self):
        return self.__builder__.schema
    def __getattr__(self, key):
        return self.__builder__.new(key)
    def __call__(self, *args):
        return self.__builder__.build(*args)
    def __merge__(self, infile=None, schema=None, otherschemas=[]):
        if infile:
            sax = Parser()
            xml = sax.parse(file=infile).root()
        elif schema:
            if isinstance(schema, wrapper):
                schema = schema.__builder__.schema
        else:
            raise Exception("No file or schema specified")
        self.__builder__.merge(xml, schema, otherschemas)

    def __xml__(self, obj, tag=None, nslist=[]):
        mx = Literal(self.__builder__.schema)
        if tag is None:
            tag = obj.__class__.__name__
            # If given a namespace list, look up the type and join
            # the namespace to the tagname
            for (prefix, ns) in nslist:
                if self.__builder__.schema.types.get((tag, ns)):
                    if prefix:
                        tag = ':'.join((prefix, tag))
                        break
        content = Content(tag, obj)
        return mx.process(content)

# These two entries variables don't do anything and aren't really
# required.  They're here to help shut up Eclipse's error checking
# a bit.
datatypes = None
entity = None

def load( infile=None, schema=None, name=None, otherschemas=[]):
    if infile:
        basename = os.path.split(infile)[-1]
        sax = Parser()
        xml= sax.parse(file=infile).root()
    elif schema:
        basename = schema.tns[0]
        xml=None
    else:
        raise Exception("No file or schema specified")

    if not name:
        basename = basename.split('.')
        if basename[-1].lower() == 'xsd':
            basename = basename[:-1]
        name = '_'.join(basename)

    glb = globals()
    if not glb.get(name):
        b = builder(xml, schema, otherschemas)
        glb[name] = wrapper(b)

# vim: ts=4 sts=4 sw=4 expandtab:
