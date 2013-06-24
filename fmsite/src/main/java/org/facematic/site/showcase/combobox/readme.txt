Данный пример демонстрирует способ описания ComboBox с биндингом на Enum.
При этом для отображения значения можно использовать как значение enum-поля, 
так и значение любого дополнительного поля, описанного в Enum.

Значение enum:
	<ComboBox name="comboBox" [b]enum="org.facematic.site.showcase.combobox.EnumForCombo"[/b] immediate="true" onChange="comboChanged"/>
	
Значение дополнительного поля:
	<ComboBox name="comboBoxB" [b]enum="org.facematic.site.showcase.combobox.EnumForCombo[font color='red']/description[/font]"[/b] immediate="true" onChange="comboChanged"/>
	
 

