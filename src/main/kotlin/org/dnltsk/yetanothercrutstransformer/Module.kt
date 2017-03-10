package org.dnltsk.yetanothercrutstransformer

import com.google.inject.Binder
import com.google.inject.Module

open class Module : Module {

    override fun configure(binder: Binder) {
        //nothing to do here because every class is simply implemented once
    }

}