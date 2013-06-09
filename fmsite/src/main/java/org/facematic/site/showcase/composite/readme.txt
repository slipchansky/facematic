[h1]Введение[/h1]
Композиция - это средство, с помощью которого вы можете собрать сложный интерфейс из отдельных частей, 
каждая из которых имеет сколь угодно сложную реализацию, и в свою очередь также собрана из отдельных 
еще более мелких (но при этом не менее сложных частей).

Для каждой из частей создается свое собственное описание, которое может иметь или не иметь собственный контроллер.
Используя элемент, описаный в виде <composite src="..."> вы добавляете эту часть в свой контент так же, как добавляете элемент Button.

Например:
Описав контент таким образом:
<TabSheet>
   <composite caption="One" src="com...Tab1Content" />
   <composite caption="One" src="com...Tab2Content" />
   <composite caption="One" src="com...Tab3Content" />
</TanSheet>

Вы получите набор закладок, каждая из которых будет построена и будет работать в соответствии с реализациями, описанными com...Tab1Content ... com...Tab3Content, 
сколь бы сложными эти реализации ни были.

Ключевыми атрибутами composite являются src и controller.

<composite src="..."  controller="">

[h2]composite.src[/h2]
src - это обязательный атрибут композита. [u][b]В общем случае[/b][/u] он содержит application-wide квалифицированное имя view, который должен быть построен в точке использования композита.
(Замечание "в общем случае" существенно, так как при использовании компоситов в рамках комплекса, src может указывать на ресурсы в пространстве имен конкретного комплекса).

[h2]composite.controller[/h2]
Как мы помним из описания принципов использования контроллера (Facematic basics -> Controller -> readme.txt), контроллер назначается на этапе описания контента. 
Но так как, имплементируя однажды созданный контент в форме композита нам может потребоваться специфическое поведение, composite позволяет переопределять 
класс контроллера в точке использования композита. В таких случаях описание <composite> должно указывать на класс, экземпляр которого будет обслуживать 
вставляемый контент в рамках данного композита. Безусловно, этот класс может быть наследником того класса, который прописан контроллером для имплементируемого контента.


[h1]UseCase[/h1]
1. был создан некий контент 

1.1.   с квалифицированным именем: org/facematic/content/SomeContent.xml
  
1.2.  и имеющий разметку вида
  <content ... controller="org.facematic.content.SomeContentController" >
 ...
 </content>

1.3. SomeContentController.java:
package org.facematic.content;
...
class SomeContentController {
   @FmReaction
   void onSave () {... do something ... }
}

2. Нам понадобилось "врезать" контент org.facematic.content.SomeContent где-то между двумя кнопками и переопределить действие onSave (), 
оставив остальное поведение без изменений.    
Для того, чтобы осуществить это делаем:


2.1. Описываем "вставку" контента  SomeContent
  <content ... >
  ...
  <Button caption="One" ... />
  [font color="darkred"]<composite src="org/facematic/content/SomeContent.xml" controller="[b]org.facematic.content.CustomSomeController[/b]">[/font]
  <Button caption="Two" ... />
  ...  
  </content>

2.2.  CustomSomeController.java

package org.facematic.content;
class CustomSomeController extends SomeContentController {
  ...
  void onSave () {... do something else ...}
  ...
}
 
В некоторых редких случаях наоборот, бывает нужно отменить привязку вставляемого контента от предписанного контроллера и связать контент с контроллером контента, в который вставляется композит.
В таких случаях в качестве значения атрибута controller разметки composite нужно указать "none" или "parent" (эти значения эквивалентны по действию). 


[h1]parents and siblings[/h1]
Как вы понимаете, возможность компоновать сложный контент из частей предполагает, что в ряде случаев нам понадобится управлять поведением совокупного контента 
как снизу вверх (от контроллера владеющего контента - поведением владеемых контентов), так и наоборот (от контроллеров владеемых контентов - поведением владеющего контента). 
Иногда нам понадобятся также и горизонтальное управление (взаимодействие владеемых контентов). 
Обеспечение такой возможности построено с учетом одного фундаментального принципа: владеемые контенты (и их контроллеры) ничего не знают друг о друге, но каждый вконтроллер 
владеемого контента знает контроллер своего контента-владеьлца, а каждый контроллер контента-владельца знает (или может знать) все контроллеры контентов, которыми он 
владеет непосредственно. Кроме того, контроллеры знают только друг друга, но не могут ссылаться на элементы view другого контроллера. Все воздействия на чужие view происходят 
при посредничестве соответствующего контроллера. 

Для обеспечения этой возможности используются специально аннотированные поля классов - контроолеров.
Для всех ссылок между контроллерами используется аннотация @FmController(name="...").

При этом ссылка на контроллер владельца аннотируется зарезервированным именем  "parent", например:
 @FmController(name="parent")
 SomeController parent;
 
 или
 
 @FmController
 SomeController parent;

 или
 
 @FmController(name="parent")
 SomeController parentController;
 Правила привязки к имени - такие же как правила для @FmViewComponent
 
Ссылка на контроллер владеемого контента аннотируется именем, указанным в значении атрибута "name" соответствующего <composite>

Например:

<content controller="SomeController" >
   ...
   <composite name="siblingContent" controller="AnotherController">
   ...
</content>


class SomeController {
   @FmController (name="siblingContent")
   AnotherController siblingController;
   ...
}

class AnotherController {
  @FmController (name="parent")
  SomeController parentController;
}

[h2]All siblings[/h2]
В ряде случаев бывает бессмысленно перечислять все контроллеры владеемых контентов в отдельных переменных (например, когда контент - владелец собирается из 
композитов динамически в процессе выполнения приложения в конкретном контексте и при этом все контроллеры владеемых контентов имеют общий интерфейс). 
Для того, чтобы при этом не потерять связь с подчиненными контентами, можно использовать аннотированное поле - список вида:

    @FmSiblingControllers
    List<SomeCommonControllerInterface> controllers;
    
Очевидно, что в данном случае все контроллеры владеемых контентов должны имплементировать или расширять тип SomeCommonControllerInterface.
Впрочем решение вида    
    @FmSiblingControllers
    List<Object> controllers;

тоже сработает, но тогда обращаться к элементам controllers нужно будет либо в самом общем виде, либо через рефлексию.

 
[h1]Пояснения реализованному примеру[/h1]
Наш пример, несмотря на кажущуюся никчемность реализованного контента, демонстрирует все описанные выше принципы.
При этом должен заметить, что все несколько достаточно сложно. В реальной жизни все прозрачней. Здесь собрать все кейсы, 
связанные с взаимодействием контентов в рамках композитного контента.

Итак.
1. Мы имеем два класса контроллеров не находящихся в родственных зависимостях:
CompositeExample и CompositeExampleNested.
Оба класса реализуют (соверенно самостоятельно) методы onFirst() и onSecond () для обеспечения декларированных разметкой реакций на нажатие кнопок.  
  
Кроме того,  от CompositeExampleNested унаследован класс CompositeCustomController, который переопределяет реакцию onFirst().
Кроме того, CompositeCustomController имеет вертикальную ссылку на контроллер контента - владельца 
и метод - реакцию callToParent для демонстрации обращения к контроллеру контента - владельца.
CompositeCustomController также имплементирует интерфейс FmBaseController и в реализации метода init делает видимой кнопку "Call to parent".

Примечание:
Я понимаю, что это не самый красивый ход: заявить спрятанную кнопку в повторно используемом компоненте, а потом показывать ее в нужном месте. 
Но для целей данного примера этого достаточно, а механизмы, которые призваны избежать такого подхода будут описаны в примере "Наследование".

2. У нас есть разметка контента с именем org.facematic.site.showcase.composite.CompositeExampleNested, которая будет вставлена в основной контент трижды, с различными контроллерами.
Разметка содержит ссылку на контроллер класса org.facematic.site.showcase.composite.CompositeExampleNested (иными словами, контент, построенный по этой разметке 
имеет предопределенное контроллером поведение).

3. Мы собираем комплексный контент разметкой org.facematic.site.showcase.composite.CompositeExample.
 
Разметка предписывает:
3.1. Добавить композитный контент по разметке org.facematic.site.showcase.composite.CompositeExampleNested с именем first 
без уточнения контроллера (будет работать контроллер класса org.facematic.site.showcase.composite.CompositeExampleNested )
	<composite name="first"  ... src="org.facematic.site.showcase.composite.CompositeExampleNested" />


3.2. Добавить композитный контент по разметке org.facematic.site.showcase.composite.CompositeExampleNested с именем second, отменив поведение, предписаное 
разметкой контента (будет работать экземпляр контроллера собираемого контента, т.е. org.facematic.site.showcase.composite.CompositeExample )
	<composite name="second" ... src="org.facematic.site.showcase.composite.CompositeExampleNested"  ... [font color="darkred"][b]controller="none"[/b][/font] />


3.3. Добавить композитный контент по разметке org.facematic.site.showcase.composite.CompositeExampleNested с именем third, переопределив поведение, предписаное 
разметкой контента и назначив контроллером экземпляр класса org.facematic.site.showcase.composite.CompositeCustomController.
	<composite name="third" ...  src="org.facematic.site.showcase.composite.CompositeExampleNested" ... [font color="darkred"][b]controller="org.facematic.site.showcase.composite.CompositeCustomController"[/b][/font] />


3.4. Добавить группы кнопок для демонстрации прямого обращения из контроллера основного контента к контроллерам дочерних контентов. Реакция прописана явно на методы класса основного контроллера.
	<hl width="100%">
		<Button caption="*.onFirst"  onClick="*_onFirst" width="100%"/>
		<Button caption="*.onSecond" onClick="*_onSecond" width="100%"/>
	</hl>
 
 где * in [first, second, third]
 
3.5. Добавить кнопку для демонстрации возможности работать с контроллерами владеемых контентов из контроллера контента - владельца.
    Реакция прописана явно на методы класса основного контроллера.
    
 	<Button caption="Show all sibling controllers classes" onClick="showSibling"/>
  
 4. Мы создаем класс контроллера контента-владельца org.facematic.site.showcase.composite.CompositeExample
 
 4.1. Декларируем ссылки на контроллеры подвластных контентов:
    @FmController(name="first")
    CompositeExampleNested first;

    @FmController(name="second")
    CompositeExample second;
    
    @FmController(name="third")
    CompositeCustomController third;
  
  
 4.2. Декларируем список контроллеров подвластных контентов:
    @FmSiblingControllers
    List<Object> controllers;
    
 4.3. Обеспечиваем реакции на кнопки композитного контента реализовав методы onFirst (),  onSecond ()
 
 4.4. Обеспечиваем реакции на кнопки демонстрации вертикального воздействия на контроллеры подвластных контентов
 *_onFirst (), *_onSecond (), где * in [first, second, third]
 
 4.5. Обеспечиваем реакцию на кнопку демонстрации "оптовой" работы с контроллерами подчиненных контентов
   showSibling ()
 
 
 