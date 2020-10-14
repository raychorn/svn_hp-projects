def merge_dict_list(dst, src):
    stack = [(dst, src)]
    while stack:
        current_dst, current_src = stack.pop()
        for key in current_src:
            if key not in current_dst:
                current_dst[key] = current_src[key]
            else:
                if isinstance(current_src[key], dict) and isinstance(current_dst[key], dict) :
                    stack.append((current_dst[key], current_src[key]))
                elif isinstance(current_src[key], list) and isinstance(current_dst[key], list) :
                    current_dst[key] += current_src[key]
                elif isinstance(current_src[key], tuple) and isinstance(current_dst[key], tuple) :
                    current_dst[key] += current_src[key]
                else:
                    current_dst[key] = current_src[key]
    return dst


if __name__ == '__main__':
    dst = dict(a=1,b=2,c=dict(ca=31, cc=33, cd=dict(cca=1)), d=4, f=6, g=7, l=[1,2,3])
    src = dict(b='u2',c=dict(cb='u32', cd=dict(cda=dict(cdaa='u3411', cdab='u3412'))), e='u5', h=dict(i='u4321'), l=[4,5,6])
    r = merge_dict_list(dst, src)
    assert r is dst
    assert r['a'] == 1 and r['d'] == 4 and r['f'] == 6
    assert r['b'] == 'u2' and r['e'] == 'u5'
    assert dst['c'] is r['c']
    assert dst['c']['cd'] is r['c']['cd']
    assert r['c']['cd']['cda']['cdaa'] == 'u3411'
    assert r['c']['cd']['cda']['cdab'] == 'u3412'
    assert r['g'] == 7
    assert src['h'] is r['h']
    assert r['l'] == [1,2,3,4,5,6]

    from pprint import pprint
    pprint(r)