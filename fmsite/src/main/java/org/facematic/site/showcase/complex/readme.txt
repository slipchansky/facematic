Этот пример показывает способ описания всех элементов представления в одном месте.
[h1]UseCase[/h1]
Вы уже имеете описания нужнух вам элементов в проекте.
Вам необходимо скомпоновать новый элемент из уже имеющихся, но при этом нужно что - то подправить в уже описанных элементах. 
Где-то добавить кнопку, где-то изменить текст, где - то переопределить или доопределить реакцию.
Для того, чтобы не порождать новые промежуточные расширения (если, конечно эти расширения не будут использоваться в других частях проекта), используется конструкция <complex>.


[h1]Complex[/h1]
Complex content всегда имеет структуру вида
<complex ...>
   <content >
      Здесь мы включаем в результирующий контент те элементы, при этом в качестве src мы можем использовать имена частей, описанных в данном комплексе.
      Но конечно, можно использовать части, описаные вне данного комплекса так же, как это делается при использовании традиционного composite 
      <composite src="..." >   
      ...
   </content>
   
   ...
   Здесь описываются части, которые можно использовать при компоновке результирующего контента, описанного тегом <content>
   Для описания частей можно использовать part, extension. В отличие от внекомплексного extension, extension внутри комплекса должно иметь имя.
   extension может расширять как внутрикомплексные, так и внекомплексные части.   
   
   <part name="part-name" >
      ...
   </part>
   
   <extension name="...">
   </extension>
    
</complex> 

В данном примере реализован TabSheet с тремя закладками.
Поведение контента определено контроллером всего комплекса, реализованным классом org.facematic.site.showcase.complex.ComplexExample.

[h2]content[/h2]
TabSheet описан конструкцией:
	<content sizeFull="true" expandRatio="100">
		<TabSheet sizeFull="true" expandRatio="100">
			<composite src="tabBase" caption="BaseTab" />
			<composite src="first" caption="FirstTab" />
			<composite src="second" caption="SecondTab" />
		</TabSheet>
	</content>
 


[h2]tabBase[/h2]
Базовая закладка описана явно в <part name="tabBase">. Надо обратить внимание на:
1. Реакции на нажатие кнопок базовой закладки обрабатываются контроллером комплекса org.facematic.site.showcase.complex.ComplexExample.
2. Подписи кнопок - формальные (определены подстановками ${firstButtonCaption}, ${secondButtonCaption}), кроме того предполагается подстановка для определения caption (${tabCaption})
3. Описание содержит позициз расширения 	<placeholder name="moreButtons" />
4. Это описание используется для получения полезного контента, поэтому здесь же оно содержит описание фактических значений текстов кнопок
		<subst name="firstButtonCaption">base-First</subst>
		<subst name="secondButtonCaption">base-Second</subst>
		<subst name="tabCaption">
		
   ${tabCaption} также определен здесь же конструкцией:
   
		<subst name="tabCaption">
		controller=ComplexExample {<br />
		    implements onFirst (), onSecond ()<br />
		}<br />
		</subst>
   
[h2]first[/h2]
Закладка first является расширением базовой, что описано конструкцией вида
<extension name="first" base="tabBase">, внутри которой изменяются фактические тексты кнопок и заголовка:
		<subst name="firstButtonCaption">first-First</subst>
		<subst name="secondButtonCaption">first-Second</subst>
		<subst name="tabCaption">Substs buttons captions, controller=ComplexExample</subst>
В остальном, компонент полученный по этому описанию будет работать так же, как и компонент, построенный по базовому описанию.


[h2]second[/h2]
Закладка second наследует свойство закладки first, меняет тексты кнопок, заголовка и добавляет третью кнопку в позицию расширения moreButtons конструкцией вида:
		<implement placeholder="moreButtons">
			<Button caption="second-Third" onClick="onThird" />
		</implement>
Третья кнопка при этом предполагает собственный метод - обработчик onThird контроллера. 
Кроме того, описание second изменяет поведение путем переопределения контроллера на org.facematic.site.showcase.complex.ComplexExample$Sub
Контроллер, реализованный классом org.facematic.site.showcase.complex.ComplexExample$Sub наследует реакции базового контроллера и переопределяет реакцию onSecond и добавляет реакцию onThird.
  