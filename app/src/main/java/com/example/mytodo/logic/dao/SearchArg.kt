package com.example.mytodo.logic.dao

import com.example.mytodo.logic.Cmd

class SearchArg(var cmd: Cmd, var value: Any?) {
    constructor(cmd: Cmd) : this(cmd,null)
}