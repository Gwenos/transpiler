class Test {

	public static int f(){
		return 1;
	}
	public static void main(String[] args) {
//		commentaire
//				commentaire
		int a = 5;
		double b = 2.3;
		String str = "hello";
		String c = str + str;
		double d = a + b;
		int test = a + f();

		if (1 + 1 == 5)	f();
		else if (1 + 1 == 5) f();
		else f();

		if (1 + 1 == 4) {
			f();
		}
		if (a + b > 3 && !false){
			f();
		}else if(1>2){
			f();
		}else if(2>1){
			f();
		}else {
			f();
			if(1>3){
				f();
			}
		}
		while (str=="hello"){
			f();
		}
	}
}
