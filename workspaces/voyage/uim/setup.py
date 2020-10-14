import sys
if sys.version < '2.6':
    sys.exit('ERROR: Sorry, python 2.6 is required for this application.')

sys.path.append('src')    
from distutils.core import setup

import py2exe
import os, glob, fnmatch  

def opj(*args):
    path = os.path.join(*args)
    return os.path.normpath(path)

def find_data_files(srcdir, *wildcards, **kw):
    # get a list of all files under the srcdir matching wildcards,
    # returned in a format to be used for install_data
    def walk_helper(arg, dirname, files):
        if '.svn' in dirname:
            return
        names = []
        lst, wildcards = arg
        for wc in wildcards:
            wc_name = opj(dirname, wc)
            for f in files:
                filename = opj(dirname, f)

                if fnmatch.fnmatch(filename, wc_name) and not os.path.isdir(filename):
                    names.append(filename)
        if names:
            lst.append( (dirname, names ) )

    file_list = []
    recursive = kw.get('recursive', True)
    if recursive:
        os.path.walk(srcdir, walk_helper, (file_list, wildcards))
    else:
        walk_helper((file_list, wildcards),
                    srcdir,
                    [os.path.basename(f) for f in glob.glob(opj(srcdir, '*'))])
    return file_list

pkgs =      ['icdb', 'utils', 'vmware' ]
                 
datafiles = [ ('.', ['README.txt', 'ancillary.txt', 'config.json.skel']) ] + \
            [ ('share/db', [] )] + \
            find_data_files('static', '*') + \
            find_data_files('share','*') + \
            find_data_files('bin', '*') + \
            find_data_files('Microsoft.VC90.CRT', '*.*')

excludes = ["pywin", "pywin.debugger", "pywin.debugger.dbgcon",
            "pywin.dialogs", "pywin.dialogs.list" ]

includes = ['web', 'web.wsgiserver', 'web.contrib', 
            'suds', 'M2Crypto', 
            'email',
                'email.base64mime',
                'email.charset',
                'email.encoders',
                'email.errors',
                'email.feedparser',
                'email.generator',
                'email.header',
                'email.iterators',
                'email.message',
                'email.parser',
                'email.quoprimime',
                'email.utils',
                'email._parseaddr',
                'email.mime', 
                'email.mime.application', 
                'email.mime.audio', 
                'email.mime.base', 
                'email.mime.image', 
                'email.mime.message', 
                'email.mime.multipart', 
                'email.mime.nonmultipart', 
                'email.mime.text' 
        ]

class Target:
    def __init__(self, **kw):
        self.__dict__.update(kw)
        # for the versioninfo resources
        self.version = "7.0.0.0"                  # build.py magic comment
        self.company_name = "HP"
        self.copyright = "Copyright 2009 Hewlett-Packard Development Company, L.P."
        self.name = "HP Insight Control for vCenter UI Manager"
        self.description = "HP Insight Control for vCenter UI Manager Service"

manifest_template = '''
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<assembly xmlns="urn:schemas-microsoft-com:asm.v1" manifestVersion="1.0">
<assemblyIdentity
    version="5.0.0.0"
    processorArchitecture="x86"
    name="%(prog)s"
    type="win32"
/>
<description>%(prog)s Program</description>
<dependency>
    <dependentAssembly>
        <assemblyIdentity 
            type="win32" 
            name="Microsoft.VC90.CRT" 
            version="9.0.21022.8" 
            processorArchitecture="x86" 
            publicKeyToken="1fc8b3b9a1e18e3b"
            language="*"
        />
    </dependentAssembly>
</dependency>
</assembly>
'''

RT_MANIFEST = 24

hpuim_service = Target(
    # used for the versioninfo resource
    description = "HP Insight Control for vCenter UI Manager",
    # What to build
    modules = [ "hpuim" ],
    cmdline_style = 'pywin32',
    other_resources = [(RT_MANIFEST, 1, manifest_template % dict(prog="hpuim"))],
    dest_base = "hpuim",
    icon_resources = [(0, "hp.ico")]
    )

setup(
    name='hpuim',
    version='7.0.0.0',                            # build.py magic comment
    package_dir = {'': 'src'},
    description='HP Insight Control for vCenter UI Manager',
    packages = pkgs,
    options = {"py2exe": 
                {
                 "bundle_files" : 3,
                 "compressed" : 1,
                 "optimize" : 2,
                 "excludes" : excludes,
                 "includes" : includes,
                 "dll_excludes": ['w9xpopen.exe', 'mswsock.dll', 'powrprof.dll'],
                }
              }, 
    zipfile = "hpuim.zip",
    data_files = datafiles,
    #console = ['src\uim.py'],
    service = [ hpuim_service ],    
    ) 

#os.system('ren dist uim')
#from distutils.archive_util import make_zipfile
#make_zipfile('uim', 'uim')
