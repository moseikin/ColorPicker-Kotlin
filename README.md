# ColorPicker-Kotlin
dependencies {
    implementation 'com.github.moseikin:ColorPicker-Kotlin:1.7'
}

Add <com.mosugu.colorpicker.ColorPickerView> in your XML.

View both dimensions became min of lyout_width and layout_height.
If set wrap_contect or match_parent - in both cases view will occupy all parent wigth and became the same height
If need to create view less wider, than parent, put exact value in lyout_width or layout_height parameter.

show_alpha_scale parameter determines will alpha scale be shown.
show_main_colors - for 8 color rectangles at the right side of view : red, orange, yellow, green, cyan, blue, violet, black.
Both of theese parameters are true by default

Data.getLiveData() or Data.getColorArgb returns color Int.
