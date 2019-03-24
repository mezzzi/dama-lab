var hi = {'a':2,'b':3, 'c':4};
for(h in hi){
	console.log(hi[h]);
	}
process.exit(1);
var b = {left:true};
console.log(b.right);
console.log(b.left);
console.log(b.right === false);
process.exit(1);

var fruits = ["Banana", "Orange", "Apple", "Mango"];
fruits.splice(2, 1);        // Removes the first element of fruit
console.log(fruits);
process.exit(1);
console.log("100px".replace('px', ''));
process.exit(1);

Function.prototype.method = function (name, func) {
    if(!this.prototype[name]){
        this.prototype[name] = func;
        return this;
    }
};

// initialized matrix/multi-dimensional array
Array.matrix = function (m, n, initial) {
	var a, i, j, mat = [];
	for (i = 0; i < m; i += 1) {
		a = [];
		for (j = 0; j < n; j += 1) {
			a[j] = initial;
		}
		mat[i] = a;
	}
	return mat;
};
var myMatrix = Array.matrix(8, 8, 0);
console.log('element 7, 7 of the initialized matrix is: '+myMatrix[7][7]); // 0
console.log('matrix is: '+myMatrix);
process.exit(1);

Array.method('isEqualTo', function (arr) {

    var i;

    if (!arr) {
    	// note this can not be null or undefined
    	// since a method can not be called on either
        return this.length === 0;
    }
    if (this.length !== arr.length) {
        return false;
    }
    for (i = 0; i < this.length; i += 1) {
        if (this[i] !== arr[i]) {
            return false;
        }
    }
    return true;        

});

var arr1 = [];
var arr2;
console.log(arr1.isEqualTo(arr2));
process.exit(1);

var SETTING = {};

var makeOb = function () {

	SETTING.A = 1;

	return {

		getA: function () {
			return SETTING.A;
		},

		changeA: function (newA) {
			SETTING.A = newA;
		}

	};

};

var a, b;
a = makeOb();
b = makeOb();

a.changeA(10);

console.log('A of a: '+a.getA());
console.log('A of b: '+b.getA());

process.exit(1);


var defaultparam = function (a, b, c) {
	console.log(arguments);
	console.log('a is '+a);
	console.log('b is '+b);
	console.log('c is '+c);
};
defaultparam(1,2,3);
console.log('--------------');
defaultparam(1,2);
console.log('--------------');
defaultparam(1);
process.exit(1);

var a = [1,2,3,4];
var b = a.slice(0);
a[0] = 10;
console.log(a);
console.log(b);
process.exit(1);

var a = [[],[]];
a[0].push(1);
a[1].push(3);
a[0].push(4);
console.log(a[0][1]);

process.exit(1);

arr = [];
for (var i=0; i < 4; i++){
	arr[i] = [];
	for (var j=0; j < 4; j++){
		arr[i][j] = i + j;
	}	
}

blah = {

	me: 'hello',

	printMe: function(){
		console.log(this.me);
	}

};

arr1 = [1, 2];
arr2 = arr1.slice(0);
arr1[1] = 3;

Function.prototype.method = function (name, func) {
    if(!this.prototype[name]){
        this.prototype[name] = func;
        return this;
    }
};

Array.method('isEqualTo', function(arr){
    var i;

    for (i = 0; i < this.length; i += 1){
        if (this[i] !== arr[i]){
            return false;
        }
    }
    return true;        
});

console.log(arr2.isEqualTo(arr1));
