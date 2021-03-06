package com.cuieney.rxpay_compile

import com.cuieney.rxpay_annotation.WX
import com.cuieney.rxpay_compile.Constant.ANNOTATION_TYPE_WX
import com.google.auto.service.AutoService
import java.io.IOException
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes(ANNOTATION_TYPE_WX)
class WxPayActivityProcessor : AbstractProcessor() {

    companion object {
         lateinit var mFiler: Filer
         lateinit var msg: Messager
    }

    @Synchronized override fun init(processingEnvironment: ProcessingEnvironment) {
        super.init(processingEnvironment)
        mFiler = processingEnvironment.filer
        msg = processingEnvironment.messager
    }


    override fun process(set: Set<TypeElement>, roundEnvironment: RoundEnvironment): Boolean {
        val routeElements = roundEnvironment.getElementsAnnotatedWith(WX::class.java)
        for (element in routeElements) {
            msg.printMessage(Diagnostic.Kind.WARNING,element.simpleName)

            val wx = element.getAnnotation(WX::class.java)
            val packageName = wx.packageName
            if (packageName.isEmpty()) {
                return false
            }
            try {
                GenerateWXActivityHelper(packageName + ".wxapi").generateAct(mFiler)
            } catch (e: IOException) {
                msg.printMessage(Diagnostic.Kind.OTHER, "Rxpay: " + e.message)
                e.printStackTrace()
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
                msg.printMessage(Diagnostic.Kind.OTHER, "Rxpay: " + e.message)
            }

        }
        return false
    }
}