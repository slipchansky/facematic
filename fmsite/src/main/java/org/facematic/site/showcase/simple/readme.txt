[h1]XML Markup vs Java Code. Simple view elements.[/h1]

Ничего особенного. Просто другая форма описания элементов интерфейса.

Просто вместо Java - кода вида 
		OptionGroup optionGroup = new OptionGroup();
		optionGroup.setCaption("OptionGroup");
		optionGroup.setHeight ("50");
		optionGroup.addItem("One");
		optionGroup.addItem("Two");
		optionGroup.addItem("Thee");

Мы используем XML - конструкцию вида
				<OptionGroup caption="OptionGroup" height="50">
					<item>One</item>
					<item>Two</item>
					<item>Three</item>
				</OptionGroup>

Знаю, знаю. Java - код можно было написать компактнее. Но как по мне, XML все - равно выразительнее. Но спорить не буду. Дело вкуса. 
Замечу лишь, что возможность лаконично описывать конструкцию представления XML-кодом - не главное преимущество facematic.  
		