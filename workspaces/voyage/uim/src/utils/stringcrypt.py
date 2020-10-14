from M2Crypto import EVP
import base64, random
import unittest


IV = '\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0'
KEY ='\x74\x6f\x9b\x22\xf0\x2f\x7e\x9d\x13\x21\x4b\x20\x1a\x1b\x7a\x86'



def str_encrypt(s):
    cipher = EVP.Cipher(alg='aes_128_cbc', key=KEY, iv=IV, op=1)
    salt = random.getrandbits(64)
    p = "%016x" % salt
    p += (s or '')
    p = p.encode('utf8')
    p = cipher.update(p) + cipher.final()
    
    return base64.b64encode(p)

def str_decrypt(s):
    cipher = EVP.Cipher(alg='aes_128_cbc', key=KEY, iv=IV, op=0)
    p = base64.b64decode(s)
    p = cipher.update(p) + cipher.final()
    p = p.decode('utf8')
    
    return p[16:]
    
    
class StringCryptTesting(unittest.TestCase):
    
    def test_encrypt_decrypt(self):
        
        s = "Testing 1234567890 1234567890"
        es = str_encrypt(s)
        ds = str_decrypt(es)
        
        print 'original string:', s
        print 'encrypted string:', es
        print 'decrypted string:', ds
        
        self.assertEqual(s, ds )        
        
if __name__ == '__main__':

    import argparse

    parser = argparse.ArgumentParser()
    parser.add_argument('-e', '--encrypt')
    parser.add_argument('-d', '--decrypt')
    args = parser.parse_args()
    if args.encrypt :
        print str_encrypt(args.encrypt)
    elif args.decrypt :
        print str_decrypt(args.decrypt)
    else :    
        unittest.main()
    