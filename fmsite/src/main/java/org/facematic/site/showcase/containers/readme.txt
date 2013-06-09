[h1]XML Markup vs Java Code. Containers.[/h1]

Так же, как и в случае с Simple view elements, facematic предлагает использовать XML разметку для описания контейнеров. 
При этом наполнения контейнера элементами описывается по правилам XML.
Так, конструкция
 
	<hl caption="HorizontalLayout">
		<Button caption="First" width="150" />
		<Button caption="Second" width="150" />
		<Button caption="Third" width="150" />
	</hl>

Эквивалентна Java code вида

HorizontalLayout hl = new HorizontalLayout ();
hl.addComponent (new Button ("First")); // ну правда здесь нельзя сразу же указать и ширину кнопки, но закроем на это глаза.
hl.addComponent (new Button ("Second"));  
hl.addComponent (new Button ("Third"));  
		