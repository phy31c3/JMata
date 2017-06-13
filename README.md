# JMata
Automata-based programming library for Java.  
For more information please see the [wiki](https://github.com/phy31c3/JMata/wiki).
## Example Code
```
JMata.initialize(log -> System.out.println(log));

JMata.buildMachine(SampleMachine.class, builder ->
{
	builder.ifPresentThenIgnoreThis(definer ->
	{
		definer.defineStartState(Start.class)
		       .whenEnter(Start::enter)
		       .whenInput("start").switchTo(Main.class)
		       .whenExit(Start::exit)
		       .apply()
		
		       .defineState(Main.class)
		       .whenEnter(Main::enter)
		       .apply()
		
		       .build();
	});
});

JMata.runMachine(SampleMachine.class);

JMata.inputTo(SampleMachine.class, "start");
```
## License
```$xslt
Copyright 2016 Jongsun Yoo

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```