Function.prototype.method = function (name, func) {
	if(!this.prototype[name]){
		this.prototype[name] = func;
		return this;
	}
};

if (typeof Object.create !== 'function') {
	Object.create = function (o) {
		var F = function () {};
		F.prototype = o;
		return new F();
	};
}

// ajax request with a call back
function loadDoc(url, cFunction) {
  var xhttp;
  xhttp=new XMLHttpRequest();
  xhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
      cFunction(this);
    }
  };
  xhttp.open("GET", url, true);
  xhttp.send();
}

function myFunction(xhttp) {
  document.getElementById("demo").innerHTML =
  xhttp.responseText;
}

// ajax request no call back
function loadDoc() {
  var xhttp = new XMLHttpRequest();
  xhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
      document.getElementById("demo").innerHTML =
      this.responseText;
    }
  };
  xhttp.open("GET", "ajax_info.txt", true);
  xhttp.send();
}

// count the occurence of words in a text
var i;
var word;
var text =
"This oracle of comfort has so pleased me, " +
"That when I am in heaven I shall desire " +
"To see what this child does, " +
"and praise my Constructor.";
var words = text.toLowerCase( ).split(/[\s,.]+/);
var count = {};
for (i = 0; i < words.length; i += 1) {
	word = words[i];
	// the following if caluse filters out count.constructor inherited from Object.prototype	
	if (typeof count[word] == 'number') { 
		count[word] += 1;
	} else {
		count[word] = 1;
	}
}
for (name in count){
	if (count.hasOwnProperty(name)){
		console.log(name+': '+count[name]);
	}
}
process.exit(1);

// wowowoo undefined and NaN are not
// constants, they are global variables, which means
// you can change their values if you want to get nasty,
// but please don't do its.

// well testing for the arrayness of an
// array is a hell of a hassle, especially
// if the array was created in another frame
// or window, but the following test or function
// written by a dear friend comes to the rescue
var isArray = function(my_value){
	return  my_value && typeof my_value === 'object' &&
		typeof my_value.length === 'number' &&
		!(my_value.propertyIsEnumerable('length'));
};

// if you don't care about testing an array
// created in another window, then you can use
// the following simpler function, the constructor
// it seems is more trustworthy than typeof
var isArraySimpler = function(my_value){
	return my_value && typeof my_value === 'object' &&
		my_value.constructor === Array;
};

// after a while it really gets confusing,
// so it might be wise to write your own i
// isNumber function, something like the 
// one shown below, the secret is in the 
// fact that isFinite returns false for
// both NaN and Infinity
var isNumber = function isNumber(value) { 
	return typeof value === 'number' && isFinite(value);
};

// this one is really painful
console.log('is nan equal to nan: '+ (NaN === NaN));
console.log('is nan not equal to nan: '+ (NaN !== NaN));
console.log('is nan NaN? '+isNaN(NaN));
// a nifty way to convert a string to a number
// is to prcede the number wit the + sign
console.log(+'67t');
console.log('type of NaN is: '+typeof NaN);
process.exit(1);

// wow you won't believe what you are about to see 
// until you see it, so brace yourself my friend
// so don't trust binary decimal point operations
// convert your numbers and do your arithmetic
// in floating point instead, floating point
// arithmetic is guranteed to be exact
console.log(0.2+0.1);
process.exit(1);

// always provide the radix for parseInt
console.log(parseInt('08', 10));
process.exit(1);
// have you ever wondered what the type of
// /a/ might be, a function or an object,
// let's find out together
console.log(typeof /a/);
process.exit(1);

// String.fromCharCode generates string from
// a series of numbers, which basically is 
// the opposite of toCharCode
var a = String.fromCharCode(67, 97, 116);
// a is 'Cat'

// there are as well the good old
// toUpperCase and toLowerCase

// well there is a substring method too,
// but sadly, it doesn't adjust for negative
// indexes like slice, so be cool and use
// slice and avoid mister substring

// split, as the name suggests splits 
// the string into components according
// to a separator or a regexep provided
// to it, not the g flag is ignored both
// in split and slice
// watch out for various quirky behaviors,
// some of which are outlined below
var digits = '0123456789';
var a = digits.split('', 5);
// a is ['0', '1', '2', '3', '456789']
var ip = '192.168.1.0';
var b = ip.split('.');
// b is ['192', '168', '1', '0']
var c = '|a|b|c|'.split('|');
// c is ['', 'a', 'b', 'c', '']
var text = 'last, first ,middle';
var d = text.split(/\s*,\s*/);
// d is [
// 'last',
// 'first',
// 'middle'
// ]
var e = text.split(/\s*(,)\s*/);
// e is [
// 'last',
// ',',
// 'first',
// ',',
// 'middle'
// ]
var f = '|a|b|c|'.split(/\|/);
// f is ['a', 'b', 'c'] on some systems, a
// slice adds string.length to position
// if the given position is negative
// the second parameter defaults to string.length
// note also that slice returns a brand new string,
// not a shallow copy
var text = 'and in it he says "Any damn fool could';
var a = text.slice(18);
// a is '"Any damn fool could'
var b = text.slice(0, 3);
// b is 'and'
var c = text.slice(-5);
// c is 'could'
var d = text.slice(19, 32);
// d is 'Any damn fool

// search is like index of, except that it
// it takes regexep instead of a string
var text = 'and in it he says "Any damn fool could';
var pos = text.search(/["']/); // pos is 18

// hey wellcome to the entitify function
String.method('entityify', function () {
	var character = {
		'<' : '&lt;',
		'>' : '&gt;',
		'&' : '&amp;',
		'"' : '&quot;'
	};
	// Return the string.entityify method, which
	// returns the result of calling the replace method.
	// Its replaceValue function returns the result of
	// looking a character up in an object. This use of
	// an object usually outperforms switch statements.
	return function () {
		return this.replace(/[<>&"]/g, function (c) {
			return character[c];
		});
	};
}());
console.log("<&>".entityify( )); // &lt;&amp;&gt;
process.exit(1);

// replace operation of string
// it takes in either a string or a regexep
// if the g flag is present, it replaces all 
// the occurences of the pattern
var oldareacode = /\((\d{3})\)/g;
var p = '(555)666-1212'.replace(oldareacode, '$1-');
// note that the $ sign has a special meaning here
// for example above, it means make the replace
// value that of the value of captured group 1
console.log('replaced p is: '+p);
process.exit(1);

// p is '555-555-1212'

// string.match maches a string against a regexp,
// how it does it depends on the presence or absence 
// of the g flag
var text = '<html><body bgcolor=linen><p>' +
'This is <b>bold<\/b>!<\/p><\/body><\/html>';
var tags = /<(\/?)([A-Za-z]+)([^<>]*)>/g;
var a, i;
a = text.match(tags);
for (i = 0; i < a.length; i += 1) {
	console.log('// [' + i + '] ' + a[i]);
}
// but when I tried it, it behaved very wierdly without
// the g flag, some how it must be very important
process.exit(1);

// localeCompareAt compares strings, returns 
// negative if the first string is less than 
// the second, note nobody knows the local
// rule used to compare the strings
var m = ['AAA', 'A', 'aa', 'a', 'Aa', 'aaa'];
m.sort(function (a, b) {
	return a.localeCompare(b);
});
console.log('m sorted using localCompareAt: '+m);
console.log('is a greater than b? '+ 'a'.localeCompare('b'));
process.exit(1);

// lastIndexOf is like indexOf, except that it
// searches from the end of the string as
// opposed to from the beginning like index of
var text = 'Mississippi';
var p = text.lastIndexOf('ss'); // p is 5
p = text.lastIndexOf('ss', 3); // p is 2
p = text.lastIndexOf('ss', 6); // p is 5
console.log('index of ss from pos 6 using last index of is: '+p);
process.exit(1);

// the index of operator, searches for a substring within 
// a string
var text = 'Mississippi';
var p = text.indexOf('ss'); // p is 2
p = text.indexOf('ss', 3); // p is 5
p = text.indexOf('ss', 6); // p is -1
console.log('index of ss is from pos 6 is: '+p);
process.exit(1);

// the concat operator
var s = 'c'.concat('a','t');
console.log('the concatenated word is: '+s);
process.exit(1);

// ha charCodeAt here is a surprise for you my friend
var name = 'Curly';
var initial = name.charCodeAt(0); // initial is 67
console.log('char at 0 of curly is: '+initial);
process.exit(1);

// charAt and its implementation using the string's 
// slice method, hola hola baby
var name = 'Curly';
var initial = name.charAt(0); // initial is 'C'
String.method('charAt', function (pos) {
	return this.slice(pos, pos + 1);
});

// the regex test method and its implementation using
// the exec method
var b = /&.+;/.test('frank &amp; beans');
console.log('is b true, lets check it out: '+b);
process.exit(1);

// b is true
RegExp.method('test', function (string) {
	return this.exec(string) !== null;
});

// baclk to regex yola
// For each tag or text, produce an array containing
// [0] The full matched tag or text
// [1] The tag name
// [2] The /, if there is one
// [3] The attributes, if any
var text = '<html><body bgcolor=linen><p>' +
'This is <b>bold<\/b>!<\/p><\/body><\/html>';
var tags = /<(\/?)([A-Za-z]+)([^<>]*)>/g;
var a, i;
while ((a = tags.exec(text))) {
	for (i = 0; i < a.length; i += 1) {
		console.log(('// [' + i + '] ' + a[i]));
	}
}
process.exit(1);

// the has own property thingathingo
var a = {member: true};
var b = Object.create(a); // from Chapter 3
var t = a.hasOwnProperty('member'); // t is true
var u = b.hasOwnProperty('member'); // u is false
var v = b.member; // v is true
console.log('a has own property member: '+t);
console.log('b has own property member: '+u);
console.log('b.memebr is true: '+v);
process.exit(1);

// methods for number
console.log('Hello PI: '+Math.PI.toExponential(3));
console.log('Hello fixed PI how you doing today? '+Math.PI.toFixed(2));
console.log('Hello precised PI, here we go: '+Math.PI.toPrecision(3));
console.log('Here we come binary PI: '+Math.PI.toString(2));
process.exit(1);

// function methods brace yourself mr. abuley
Function.method('bind', function (that) {
	// Return a function that will call this function as
	// though it is a method of that object.
	var method = this,
	slice = Array.prototype.slice,
	args = slice.apply(arguments, [1]);
	return function () {
		return method.apply(that,
		args.concat(slice.apply(arguments, [0])));
	};
});

var x = function () {
	console.log('your arguments are: '+Array.prototype.slice.apply(arguments, [0]));	
	return this.value;
}.bind({value: 666});
console.log('returned: '+ x('hello', 5, {1:'hiwot'})); // 666
process.exit(1);

var a = ['a', 'b', 'c'];
var r = a.unshift('?', '@');
console.log('the unshifted a is: '+a);
// a is ['?', '@', 'a', 'b', 'c']
// r is 5
process.exit(1);

// check out this implementation of the unshift method of the array
Array.method('unshift', function ( ) {
	this.splice.apply(this,
	[0, 0].concat(Array.prototype.slice.apply(arguments)));
	return this.length;
});

// I am only human after all, I am only human after all, only human after all
// yeah only human after all, only human after all, yeah.

// Function by takes a member name string and an
// optional minor comparison function and returns
// a comparison function that can be used to sort an
// array of objects that contain that member. The
// minor comparison function is used to break ties
// when the o[name] and p[name] are equal.
var s = [
	{first: 'Joe', last: 'Besser'},
	{first: 'Moe', last: 'Howard'},
	{first: 'Joe', last: 'DeRita'},
	{first: 'Shemp', last: 'Howard'},
	{first: 'Larry', last: 'Fine'},
	{first: 'Curly', last: 'Howard'}
];
var by = function (name, minor) {
	return function (o, p) {
		var a, b;
		if (o && p && typeof o === 'object' && typeof p === 'object') {
			a = o[name];
			b = p[name];
			if (a === b) {
				return typeof minor === 'function' ? minor(o, p) : 0;
			}
			if (typeof a === typeof b) {
				return a < b ? -1 : 1;
			}
			return typeof a < typeof b ? -1 : 1;
		} else {
			throw {
				name: 'Error',
				message: 'Expected an object when sorting by ' + name
			};
		}
	};
};
s.sort(by('last', by('first'))); // s is [
// {first: 'Joe', last: 'Besser'},
// {first: 'Joe', last: 'DeRita'},
// {first: 'Larry', last: 'Fine'},
// {first: 'Curly', last: 'Howard'},
// {first: 'Moe', last: 'Howard'},
// {first: 'Shemp', last: 'Howard'}
// ]
var i;
for(i=0; i < s.length; i+=1){
	console.log(s[i]['first'] +' '+s[i]['last']);
}
process.exit(1);

// Function by takes a member name string and returns
// a comparison function that can be used to sort an
// array of objects that contain that member.
var by = function (by_name) {
	return function (o, p) {
		var a, b;
		if (typeof o === 'object' && typeof p === 'object' && o && p) {
			a = o[by_name];
			b = p[by_name];
			if (a === b) {
				return 0;
			}
			if (typeof a === typeof b) {
				return a < b ? -1 : 1;
			}
			return typeof a < typeof b ? -1 : 1;
		} else {
			throw {
				name: 'Error',
				message: 'Expected an object when sorting by ' + by_name
			};
		}
	};
};

var s = [
	{first: 'Joe', last: 'Besser'},
	{first: 'Moe', last: 'Howard'},
	{first: 'Joe', last: 'DeRita'},
	{first: 'Shemp', last: 'Howard'},
	{first: 'Larry', last: 'Fine'},
	{first: 'Curly', last: 'Howard'}
];
s.sort(by('first')); // s is [
var i;
for(i=0; i < s.length; i+=1){
	console.log(s[i]['first']);
}
// {first: 'Curly', last: 'Howard'},
// {first: 'Joe', last: 'DeRita'},
// {first: 'Joe', last: 'Besser'},
// {first: 'Larry', last: 'Fine'},
// {first: 'Moe', last: 'Howard'},
// {first: 'Shemp', last: 'Howard'}
// ]
process.exit(1);

// javascript sorting really sucks
// if you want to get it right, then do it yourself
var m = ['aa', 'bb', 'a', 4, 8, 15, 16, 23, 42];
m.sort(function (a, b) {
	if (a === b) {
		return 0;
	}
	if (typeof a === typeof b) {
		return a < b ? -1 : 1;
	}
	return typeof a < typeof b ? -1 : 1;
});
console.log(m);
process.exit(1);

// don't forget array.slice, like 
// array.concat is also a shallow operation
var a = ['a', 'b', 'c'];
var b = a.slice(0, 1); // b is ['a']
var c = a.slice(1); // c is ['b',
var d = a.slice(1, 2); // d is ['b']

// implementaion of the shift method of the array
Array.method('shift', function ( ) {
	return this.splice(0, 1)[0];
});

// implemntaion of the push method of the array
Array.method('push', function ( ) {
	this.splice.apply(
		this,
		[this.length, 0].concat(Array.prototype.slice.apply(arguments))
	);
	return this.length;
});

// implementation of array.pop
Array.method('pop', function ( ) {
	return this.splice(this.length - 1, 1)[0];
});

// methods, that is the standard ones that ships with javascript out of the box just for you
// note, do not be tricked, concat does a shallow copy as shown
// by the example below, i said mind you man
var a = ['a', 'b', 'c'];
a.push('d');
var c = a.join(''); // c is 'abcd';
console.log('the joined array is: '+c);
process.exit(1);

var f = {'a':'hello there'};
var a = ['a', f, 'c'];
var b = ['x', 'y', 'z'];
var c = a.concat(b, true);
console.log(c);
f['a'] = 'hello buddy';
// c is ['a', 'b', 'c', 'x', 'y', 'z', true]
console.log(c);
process.exit(1);

// Regular expressions
console.log("into".match(/into|in/g));
process.exit(1);

// regex objects made from regex literals share one instance
// for example below, x and y are the same object
// said the author who wrote the book, but when i tested it,
// x and y turned out to be different objects, may be
// this is happening only in node, i don't know really, to be honest
function make_a_matcher( ) {
	return /a/gi;
}
var x = make_a_matcher( );
var y = make_a_matcher( );
// Beware: x and y are the same object!
x.lastIndex = 10;
console.log(y.lastIndex); // 10
process.exit(1);

var my_regexp = /"(?:\\.|[^\\\"])*"/g; // matches a javascript string
// Make a regular expression object that matches
// a JavaScript string.
// note that the double quote and the backslash have to be escaped each time
var my_regexp = new RegExp("\"(?:\\.|[^\\\\\\\"])*\"", 'g');

console.log(my_regexp.test('"hi\\\t\there"'));
process.exit(1);

var parse_number = /^-?\d+(?:\.\d*)?(?:e[+\-]?\d+)?$/i;
var test = function (num) {
	console.log('Is '+num+' a number? '+parse_number.test(num));
};
test('1'); // true
test('number'); // false
test('98.'); // true
test('132.21.86.100'); // false
test('123.45E-67'); // true
test('123.45D-67'); // false
process.exit(1);

var parse_url = /^(?:([A-Za-z]+):)?(\/{0,3})([0-9.\-A-Za-z]+)(?::(\d+))?(?:\/([^?#]*))?(?:\?([^#]*))?(?:#(.*))?$/;
var url = "http://www.ora.com:80/goodparts?q#fragment";
var result = parse_url.exec(url);
var names = ['url', 'scheme', 'slash', 'host', 'port',
'path', 'query', 'hash'];
var blanks = ' ';
var i;
for (i = 0; i < names.length; i += 1) {
	console.log(names[i] + ':' +
	blanks.substring(names[i].length), result[i]);
}
process.exit(1);

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

// Make a 4 * 4 matrix filled with zeros.
var myMatrix = Array.matrix(4, 4, 0);
console.log('element 3, 3 of the initialized matrix is: '+myMatrix[3][3]); // 0
// Method to make an identity matrix.
Array.identity = function (n) {
	var i, mat = Array.matrix(n, n, 0);
	for (i = 0; i < n; i += 1) {
		mat[i][i] = 1;
	}
	return mat;
};
myMatrix = Array.identity(4);
console.log('element 3,3 of the identity matrix is: '+myMatrix[3][3]); // 1
process.exit(1);

// multidimensional arrays
var matrix = [
	[0, 1, 2],
	[3, 4, 5],
	[6, 7, 8]
];
console.log(matrix[2][1]); // 7
process.exit(1);

// array dimensioning
Array.dim = function (dimension, initial) {
	var a = [], i;
	for (i = 0; i < dimension; i += 1) {
		a[i] = initial;
	}
	return a;
};
// Make an array containing 10 zeros.
var myArray = Array.dim(10, 0);
console.log(myArray);
process.exit();

// arrays
Array.method('reduce', function (f, value) {
	var i;
	for (i = 0; i < this.length; i += 1) {
		value = f(this[i], value);
	}
	return value;
});

// Create an array of numbers.
var data = [4, 8, 15, 16, 23, 42];
// Define two simple functions. One will add two
// numbers. The other will multiply two numbers.
var add = function (a, b) {
	return a + b;
};

var mult = function (a, b) {
	return a * b;
};
// Invoke the data's reduce method, passing in the
// add function.
var sum = data.reduce(add, 0); // sum is 108
// Invoke the reduce method again, this time passing
// in the multiply function.
var product = data.reduce(mult, 1);
// product is 7418880
console.log('sum is: '+sum);
console.log('product is: '+product);
data.total = function(){
	return this.reduce(add);
};
console.log('total is: '+data.total());
process.exit(1);

var is_array = function (value) {
	return value && typeof value === 'object' &&
	value.constructor === Array;
};

var is_array = function (value) {
	return value && typeof value === 'object' &&
	typeof value.length === 'number' &&
	typeof value.splice === 'function' &&
	!(value.propertyIsEnumerable('length'));
};

var numbers = ['one', 'two', 'three', 'four', 'five'];
console.log(numbers);
numbers.length = 3;
console.log(numbers);
numbers[numbers.length] = 'four';
numbers.push('five');
console.log(numbers);
delete numbers[2];
console.log('after deleting the third element: ');
console.log(numbers);
numbers.splice(2, 1);
console.log('after splicing the third element: ');
console.log(numbers);
var i;
for (i = 0; i < numbers.length; i++) {
	console.log(numbers[i]);
}
process.exit(1);

var myArray = [];
myArray[1000000] = true;
myArray.length // 1000001
console.log('myArray length is: '+myArray.length); // 10
process.exit(1);

var misc = [
	'string', 98.6, true, false, null, undefined,
	['nested', 'array'], {object: true}, NaN,
	Infinity
];

// inheritance
Object.method('superior', function (name) {
	var that = this,
	method = that[name];
	return function () {
		return method.apply(that, arguments);
	};
});

var mammal = function (spec) {
	var that = {};
	that.get_name = function ( ) {
		return spec.name;
	};
	that.says = function ( ) {
		return spec.saying || '';
	};
	return that;
};
var myMammal = mammal({name: 'Herb'});

var cat = function (spec) {
	spec.saying = spec.saying || 'meow';
	var that = mammal(spec);
	that.purr = function (n) {
		var i, s = '';
		for (i = 0; i < n; i += 1) {
			if (s) {
				s += '-';
			}
			s += 'r';
		}
		return s;
	};
	that.get_name = function () {
		return that.says() + ' ' + spec.name + ' ' + that.says();
	}
	return that;
};

var myCat = cat({name: 'Henrietta'});

var coolcat = function (spec) {
	var that = cat(spec),
	super_get_name = that.superior('get_name');
	that.get_name = function (n) {
		return 'like ' + super_get_name( ) + ' baby';
	};
	return that;
};
var myCoolCat = coolcat({name: 'Bix'});
var name = myCoolCat.get_name( );
// 'like meow Bix meow baby'

var myMammal = {
	name : 'Herb the Mammal',
	get_name : function ( ) {
		return this.name;
	},
	says : function ( ) {
		return this.saying || '';
	}
};

var myCat = Object.create(myMammal);
myCat.name = 'Henrietta';
myCat.saying = 'meow';
myCat.purr = function (n) {
	var i, s = '';
	for (i = 0; i < n; i += 1) {
		if (s) {
			s += '-';
		}
		s += 'r';
	}
	return s;
};
myCat.get_name = function () {
	return this.says() + ' ' + this.name + ' ' + this.says();
};

Function.method('inherits', function (Parent) {
	this.prototype = new Parent();
	return this;
});

var Cat = function (name) {
	this.name = name;
	this.saying = 'meow';
}.inherits(Mammal).method('purr', function (n) {
	var i, s = '';
	for (i = 0; i < n; i += 1) {
		if (s) {
			s += '-';
		}
		s += 'r';
	}
	return s;
}).method('get_name', function () {
	return this.says() + ' ' + this.name +
	' ' + this.says();
});

var Mammal = function (name) {
	this.name = name;
	this.purr = false;
};

Mammal.prototype.get_name = function ( ) {
	return this.name;
};

Mammal.prototype.says = function ( ) {
	return this.saying || '';
};

var myMammal = new Mammal('Herb the Mammal');
var name = myMammal.get_name( ); // 'Herb the Mammal'

var Cat = function (name) {
	this.name = name;
	this.saying = 'meow';
};
// Replace Cat.prototype with a new instance of Mammal
Cat.prototype = new Mammal();
// Augment the new prototype with
// purr and get_name methods.
Cat.prototype.purr = function (n) {
	var i, s = '';
	for (i = 0; i < n; i += 1) {
		if (s) {
			s += '-';
		}
		s += 'r';
	}
	return s;
};
Cat.prototype.get_name = function ( ) {
	return this.says( ) + ' ' + this.name + ' ' + this.says( );
};

var myCat = new Cat('Henrietta');
var says = myCat.says( ); // 'meow'
var purr = myCat.purr(5); // 'r-r-r-r-r'
var name = myCat.get_name( );
// 'meow Henrietta meow'

//console.log(typeof myMammal.purr);

var hi = function(){
	this.hola = false;
};
var there = new hi();


var hello = function(){
	this.blah = true;
};

var hell = new hello();

hell.prototype = there;
Object.prototype.holla = true;

console.log(typeof hell.holla);
process.exit();

// if the new operator were to be a method
Function.method('new', function ( ) {
	// Create a new object that inherits from the
	// constructor's prototype.
	var that = Object.create(this.prototype);
	// Invoke the constructor, binding –this- to
	// the new object.
	var other = this.apply(that, arguments);
	// If its return value isn't an object,
	// substitute the new object.
	return (typeof other === 'object' && other) || that;
});

//memorization
var memorizer = function (memo, fundamental) { // this is crazy cool
	var shell = function (n) {
		var result = memo[n];
		if (typeof result !== 'number') {
			// the nature of the recursive call is upto the fundamental function
			result = fundamental(shell, n); 
			memo[n] = result;
		}
		return result;
	};
	return shell;
};

// wait for it, the coolest part is coming
var fibonacci = memorizer([0, 1], function (shell, n) {
	return shell(n - 1) + shell(n - 2);
});

var factorial = memorizer([1, 1], function (shell, n) {
	return n * shell(n - 1);
});

console.log('factorial 10 is: '+factorial(10));
console.log('fibonacci 10 is: '+fibonacci(10));
process.exit(1);

var fibonacci = function ( ) {
	var memo = [0, 1];
	var fib = function (n) {
		var result = memo[n]; // check if the value is already computed
		if (typeof result !== 'number') {
			result = fib(n - 1) + fib(n - 2);
			memo[n] = result; // save for later use
		}
		return result;
	};
	return fib;
}();
console.log(fibonacci(10));
process.exit(1);

// currying
Function.method('curry', function ( ) {
	var slice = Array.prototype.slice,
	args = slice.apply(arguments),
	that = this; // the function obect being curried

	return function () {
		return that.apply(null, args.concat(slice.apply(arguments)));
	};
});

var add = function add(a, b){
	return a + b;
};

var add1 = add.curry(1);
console.log(add1(9));
process.exit(1);

//module
var serial_maker = function ( ) {
	// Produce an object that produces unique strings. A
	// unique string is made up of two parts: a prefix
	// and a sequence number. The object comes with
	// methods for setting the prefix and sequence
	// number, and a gensym method that produces unique
	// strings.
	var prefix = '';
	var seq = 0;

	return {

		set_prefix: function (p) {
			prefix = String(p);
		},

		set_seq: function (s) {
			seq = s;
		},

		gensym: function ( ) {
			var result = prefix + seq;
			seq += 1;
			return result;
		}
	};
};

var seqer = serial_maker( );
seqer.set_prefix('Q');
seqer.set_seq(1000);
var unique = seqer.gensym( ); // unique is "Q1000"
console.log('unique is: '+unique);

process.exit(1);

String.method('deentityify', function ( ) {
	// The entity table. It maps entity names to
	// characters.
	var entity = {
		quot: '"',
		lt: '<',
		gt: '>'
	};
	// Return the deentityify method.
	return function ( ) {
		// This is the deentityify method. It calls the string
		// replace method, looking for substrings that start
		// with '&' and end with ';'. If the characters in
		// between are in the entity table, then replace the
		// entity with the character from the table. It uses
		// a regular expression (Chapter 7).
		return this.replace(/&([^&;]+);/g, function (a, b) {
				var r = entity[b];
				return typeof r === 'string' ? r : a;
			}
		);
	};
}());
console.log('&lt;&quot;&gt;'.deentityify( )); // <">
process.exit(1);

//closures again
var myObject = function ( ) {
	var value = 0;
	return {
		increment: function (inc) {
			value += typeof inc === 'number' ? inc : 1;
		},
		getValue: function ( ) {
			return value;
		}
	};
}();
myObject.increment();
console.log('value is: '+myObject.getValue());

var quo = function (status) {
	return {
		get_status: function ( ) {
			return status;
		}
	};
};

var myQuo = quo("amazed");
console.log(myQuo.get_status( ));
process.exit(1);

var fade = function (node) { // cooler application of closures
	var level = 1;
	var step = function ( ) {
		var hex = level.toString(16);
		node.style.backgroundColor = '#FFFF' + hex + hex;
		if (level < 15) {
			level += 1;
			setTimeout(step, 100);
		}
	};
	setTimeout(step, 100);
};
fade(document.body);

// variable scope
var foo = function ( ) {
	var a = 3, b = 5;
	var bar = function ( ) {
		var b = 7, c = 11;
		// At this point, a is 3, b is 7, and c is 11
		a += b + c;
		// At this point, a is 21, b is 7, and c is 11
	};
	// At this point, a is 3, b is 5, and c is not defined
	bar( );
	// At this point, a is 21, b is 5
};

// tail recursion
var factorial = function factorial(i, a) {
	a = a || 1;
	if (i < 2) {
		return a;
	}
	return factorial(i - 1, a * i);
};

console.log('factorial of 10 is: '+factorial(10));

process.exit(1);

// Define a walk_the_DOM function that visits every
// node of the tree in HTML source order, starting
// from some given node. It invokes a function,
// passing it each node in turn. walk_the_DOM calls
// itself to process each of the child nodes.
var walk_the_DOM = function walk(node, func) {
	func(node);
	node = node.firstChild;
	while (node) {
		walk(node, func);
		node = node.nextSibling;
	}
};

// Define a getElementsByAttribute function. It
// takes an attribute name string and an optional
// matching value. It calls walk_the_DOM, passing it a
// function that looks for an attribute name in the
// node. The matching nodes are accumulated in a
// results array.
var getElementsByAttribute = function (att, value) {
	var results = [];
	walk_the_DOM(document.body, function (node) {
		var actual = node.nodeType === 1 && node.getAttribute(att);
		if (typeof actual === 'string' &&
		(actual === value || typeof value !== 'string')) {
			results.push(node);
		}
	});
	return results;
};

// recursion towers of hanoi
var hanoi = function (disc, src, aux, dst) {
	if (disc > 0) {
		hanoi(disc - 1, src, dst, aux);
		console.log('Move disc ' + disc + ' from ' + src + ' to ' + dst);
		hanoi(disc - 1, aux, src, dst);
	}
};

hanoi(3, 'Src', 'Aux', 'Dst');

// augementing a fucntion

Number.method('integer', function ( ) {
	return Math[this < 0 ? 'ceil' : 'floor'](this);
});
console.log((-10 / 3).integer()); // -3

String.method('trim', function ( ) {
	return this.replace(/^\s+|\s+$/g, '');
});
console.log('    hey there am I trimmed?  '.trim());

// exception
var add = function (a, b) {
	if (typeof a !== 'number' || typeof b !== 'number') {
		throw {
			name: 'TypeError',
			message: 'add needs numbers'
		};
	}
	return a + b;
};

var try_it = function ( ) {
	try {
		add("seven");
	} catch (e) {
		console.log(e.name + ': ' + e.message);
	}
}

try_it( );

// the bonus aguments param
var sum = function ( ) {
	var i, sum = 0;
	for (i = 0; i < arguments.length; i += 1) {
		sum += arguments[i];
	}
	return sum;
};

console.log('the sum is: '+sum(1, 2, 3, 3, 4, 5, 4));

// new constructor

var Quo = function (string) {
	this.status = string;
};

Quo.prototype.get_status = function ( ) {
	return this.status;
};

// Make an instance of Quo.
var myQuo = new Quo("confused");
console.log(myQuo.get_status( )); // confused

var randStatus = {
	status:'I am OK'
};
console.log(Quo.prototype.get_status.call(randStatus));

// that, workaround of this
var myObject = {
	value:3
};

myObject.double = function ( ) {
	var that = this; // Workaround.

	var helper = function ( ) {
		that.value = 2 * that.value; // change that with this; value will not be douled
	};

	helper( ); // Invoke helper as a function.
};

myObject.double( ); // invoke double as a method
console.log('value after double is: '+myObject.value)

// prototype

stooge = {
	'first-name':'mezi',
	head:true
};

stooge.thirdName = 'whateva';

var anotherStooge = Object.create(stooge);
anotherStooge.lastName = 'abebe';
console.log(stooge['first-name']);
console.log('type of stooge last name is: '+ typeof stooge.lastName);
console.log('anoter stooge owns thirdName? ' + anotherStooge.hasOwnProperty('thirdName'));

var name;
for(name in anotherStooge){
	if(typeof anotherStooge[name] !== 'function' && anotherStooge.hasOwnProperty(name)){
		console.log(name+' : '+anotherStooge[name]);
	}
}

// polymorphism
function sort() {
	var args = [].slice.call(arguments, 0);
	return args.sort();
}
console.log(sort('b','a','c','d'));

// named parameters
var _ = require('underscore');
var userProto = {
	name: '',
	email: '',
	alias: '',
	showInSearch: true,
	colorScheme: 'light'
};

function createUser(options) {
	return _.extend({}, userProto, options);
}

var user = createUser({name:'Mezi', email:'whatever'});
console.log('name: '+user.name+ ' email: '+user.email);

// closures
(function(){

	var arr = [], count = 1, delay = 20,
	timer, complete;

	complete = function complete(){
		console.log('arr is: '+ arr.join(','));
	};

	console.log('inside there');

	timer = function timer() {
		console.log('timer called');
		setTimeout(function inner() {
			console.log('pushing');
			arr.push(count);
			if (count < 3) {
				count += 1;
				console.log('timer started');
				timer();
			} else {
				complete();
			}
		}, 10);
	};

	timer();

}());

var o = function o () {
	var data = 1, get;
	get = function get() {
		return data;
	};

	return {
		get: get
	};
};

var ob = o();
try{
	console.log('data is: '+data);	
} catch (e){
	console.log('data is undefined');
}
console.log('data is: '+ob.get());

// hoisting
function number() {
	return 1;
} 

(function () {
	try {
		console.log('inner: '+number());		
	} catch (e){
		console.log('inner number is undefined');
	}
	var number = function number() {
		return 2;
	}
}());

var x = 1;
(function () {
	console.log(x); // very wierd x is undefined
	var x = 3;
}());

function number() {
	return 3;
} 
console.log('outer: '+number());

var obj = {

	hello : function hi (msg) {

		if(msg == 'inside'){
			console.log('hi from inside');
			return;
		}
		hi('inside');
		console.log(msg);

	},

	helloOut : function (){
		this.hello('hello from outisde');
	}

}

var Light = function() {
	this.on = false;
	this.turnon = function (){
		console.log('light is on');
	};
}

li = new Light();
li.turnon();

Light.prototype.toggle = function(){
	this.on = !this.on;
	console.log('light is turned off');
};

var tellName = function(msg, times){
	for(var i = 0; i < times; i++){
		console.log(this.name+msg);
	}
}


var objA = {
	name: 'Object A'
},

objB = {
	name: 'Object B'
};

var bound = tellName.bind(objA);
bound(' hi', 2);

tellName(' hi', 2); // this will be undefined

tellName.call(objA, ' hi', 2);
tellName.apply(objB, [' holla', 4]);

li.toggle();

(function(){

	var isOn = false,

	toggle = function toggle() {
		isOn = !isOn;
		return isOn;
	},

	getState = function getState(){
		console.log('on state is: '+isOn);
	}

	off = function off() {
		isOn = false;
		console.log('light is turned off');
	},

	lightbulb = {
		toggle: toggle,
		off: off,
		getState: getState
	};

	lightbulb.getState();
	lightbulb.toggle();
	lightbulb.getState();

}());

obj.hello('Hello Sir?');
obj.helloOut();

var url = require('url');
var parsedUrl = url.parse("http://www.google.com/profile?name=mezigebu");

console.log(parsedUrl.protocol);
console.log(parsedUrl.host);
console.log(parsedUrl.query);

var sum = function(){
	var result = 0;
	[3, 3, 3, 3].forEach(function addIt(num){ result += num; });
	return result;
}
console.log('sum is '+sum());

