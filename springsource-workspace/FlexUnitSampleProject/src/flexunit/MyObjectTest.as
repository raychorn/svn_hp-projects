package flexunit
{
	import flexunit.framework.Assert;
	
	import samples.MyObject;
	
	public class MyObjectTest
	{		
		private var classToTestRef:MyObject;

		[Before]
		public function setUp():void
		{
			this.classToTestRef = new MyObject();
		}
		
		[After]
		public function tearDown():void
		{
			this.classToTestRef = null;
		}
		
		[BeforeClass]
		public static function setUpBeforeClass():void
		{
		}
		
		[AfterClass]
		public static function tearDownAfterClass():void
		{
		}
		
		[Test]
		public function testAlwaysTrue():void
		{
			Assert.assertTrue(this.classToTestRef.alwaysTrue());
		}
		
		[Test]
		public function testGiveMeFive():void
		{
			Assert.assertEquals(this.classToTestRef.giveMeFive(), 5);
		}
		
		[Test]
		public function testMyObject():void
		{
		}
		
		[Test]
		public function testSayA():void
		{
			Assert.assertEquals(this.classToTestRef.sayA(), 'A');
		}
	}
}