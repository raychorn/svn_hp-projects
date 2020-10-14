class ddict(dict) :
    """Default Dictionary.  get returns the key value if it exists and its value is not None.
       Otherwise the default value is returned.  """
       
    def get(self, key, d=None) :
        v = dict.get(self, key, d)        
        if v == None :
           v = d
        
        if isinstance(v, dict) :
            v = ddict(v)
        return v

if __name__ == '__main__':
    import json
        
    j = '{"one": 1, "two": null}'

    d = json.loads(j, object_hook=ddict)
    print d.get('three')    
    print d.get('two', {}).get('three',{}).get('five', 5)
    
    print json.dumps(d)
