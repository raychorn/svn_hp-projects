import logging
import json

logging.basicConfig()
log = logging.getLogger(__name__)
log.setLevel(logging.DEBUG)

class MockNetworkSummary :
    def GET(self) :
        data = {
            'nics': [
				{'vmnic':'vmnic0', 'physical_nic': 'LOM:1-a'},
				{'vmnic':'vmnic1', 'physical_nic': 'LOM:2-a'},
				{'vmnic':'vmnic2', 'physical_nic': 'LOM:1-b'},
				{'vmnic':'vmnic3', 'physical_nic': 'LOM:2-b'},
				{'vmnic':'vmnic4', 'physical_nic': 'LOM:1-c'},
				{'vmnic':'vmnic5', 'physical_nic': 'LOM:2-c'},
				{'vmnic':'vmnic6', 'physical_nic': 'LOM:1-d'},
				{'vmnic':'vmnic7', 'physical_nic': 'LOM:2-d'},		
			]
        }
        
        return json.dumps(data)
        
        
class MockNetworkDiagram :
    def GET(self) :
        data = json.load(open('netdiagram_mock.json'))
        return json.dumps(data)
        
class MockClusterSummary :
    def GET(self) :
        data = {
                'cluster_name': 'Mock Cluster 1',
                'hosts': 2,
                'vms': 3
            }
            
        return json.dumps(data)
        
class MockClusterInfraSummary :
    def GET(self) :
        data = {
            "oas" : [
                    {"oa": {"enclosure_info": {"powerSupplyBays": 6, "powerSuppliesPresent": 6, "name": "Mock BladeSystem c7000", "serialNumber": "USE924MW2P", "bladeBays": 16, "enclosureName": "Mock OA-BR", "bladesPresent": 11, "fansPresent": 10, "rackName": "UnnamedRack", "fanBays": 10}, "power": {"powerConsumed": 3922}}},
                    {"oa": {"enclosure_info": {"powerSupplyBays": 4, "powerSuppliesPresent": 4, "name": "Mock BladeSystem c3000", "serialNumber": "USE924MWAY", "bladeBays": 8, "enclosureName": "Mock OA-BRC3", "bladesPresent": 5, "fansPresent": 6, "rackName": "UnnamedRack", "fanBays": 6}, "power": {"powerConsumed": 1500}}},
                ]
            }
        
        return json.dumps(data)
        
class MockLaunchTools :
    def GET(self) :
        data = {
            'launch_tools' : [
                {
                    'id': 'ilo_launch',
                    'icon_url' : "/static/img/icons/iLO_icon.png",
                    'launch_links' : [
                        {
                            'type': "LINK",
                            'label': "Google",
                            'url' : "http://www.google.com"
                        },
                        {
                            'type': "LINK",
                            'label': "Facebook",
                            'url' : "http://www.facebook.com"
                        },
                        {
                            'type': "LINK",
                            'label': "Twitter",
                            'url' : "http://www.twitter.com"
                        }
                    ]
                },
                {
                    'id': 'oa_launch',
                    'icon_url' : "/static/img/icons/oa_icon.png",
                    'launch_links' : [
                        {
                            'type': "LINK",
                            'label': "Google",
                            'url' : "http://www.google.com"
                        }
                    ]
                },
                {
                    'id': 'vc_launch',
                    'icon_url' : "/static/img/icons/vc_icon.png",
                    'launch_links' : [
                        {
                            'type': "LINK",
                            'label': "Google",
                            'url' : "http://www.google.com"
                        }
                    ]
                }
            ]
        }
        
        return json.dumps(data)