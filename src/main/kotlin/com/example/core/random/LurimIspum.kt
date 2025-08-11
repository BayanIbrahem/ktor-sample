package com.example.core.random

interface Generator<T : Any> {
    fun generate(): T
}

abstract class ListGenerator<T : Any>(
    val minLength: Int,
    val maxLength: Int,
    val itemGenerator: Generator<T>,
) : Generator<List<T>> {
    override fun generate(): List<T> = List((minLength..maxLength).random()) {
        itemGenerator.generate()
    }
}

object WordGenerator : Generator<String> {
    private val loremIpsum by lazy {
        "Lorem ipsum dolor sit amet consectetur adipiscing elit quisque faucibus ex sapien vitae pellentesque sem placerat in id cursus mi pretium tellus duis convallis tempus leo eu aenean sed diam urna tempor pulvinar vivamus fringilla lacus nec metus bibendum egestas iaculis massa nisl malesuada lacinia integer nunc posuere ut hendrerit semper vel class aptent taciti sociosqu ad litora torquent per conubia nostra inceptos himenaeos orci varius natoque penatibus et magnis dis parturient montes nascetur ridiculus mus donec rhoncus eros lobortis nulla molestie mattis scelerisque maximus eget fermentum odio phasellus non purus est efficitur laoreet mauris pharetra vestibulum fusce dictum risus".split(
            " "
        ).toSet()
    }

    override fun generate(): String = loremIpsum.random()
}

class SentenceGenerator(
    minLength: Int,
    maxLength: Int,
) : ListGenerator<String>(minLength, maxLength, WordGenerator)
