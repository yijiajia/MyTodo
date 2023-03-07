package com.example.mytodo.logic

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

fun Serializable.toByteArray(): ByteArray {
    val out = ByteArrayOutputStream()
    val os = ObjectOutputStream(out)
    os.writeObject(this)
    return out.toByteArray()
}

fun ByteArray.toObject(): Any {
    val ins = ByteArrayInputStream(this)
    val oIns = ObjectInputStream(ins)
    return oIns.readObject()
}