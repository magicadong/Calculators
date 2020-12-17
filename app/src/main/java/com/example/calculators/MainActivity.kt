package com.example.calculators

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),View.OnClickListener {
    private val currentInputNumSB = StringBuilder()
    private val numsList = mutableListOf<Int>()
    private val operatorsList = mutableListOf<String>()
    private var isNumStart = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //清空按钮
        textView3.setOnClickListener{
            clearButtonClicked(it)
        }
        //返回按钮
        imageView.setOnClickListener {
            backButtonClicked(it)
        }
        //除法
        textView6.setOnClickListener{
            operatorButtonClicked(it)
        }
        //乘法
        textView18.setOnClickListener{
            operatorButtonClicked(it)
        }
        //加法
        textView19.setOnClickListener{
            operatorButtonClicked(it)
        }
        //减法
        textView20.setOnClickListener{
            operatorButtonClicked(it)
        }

        //7
        textView7.setOnClickListener(this)
        textView10.setOnClickListener(this)
        textView11.setOnClickListener(this)
        textView8.setOnClickListener(this)
        textView12.setOnClickListener(this)
        textView13.setOnClickListener(this)
        textView9.setOnClickListener(this)
        textView14.setOnClickListener(this)
        textView15.setOnClickListener(this)
        textView16.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        numberButtonClicked(v!!)
    }

    //数字键
    private fun numberButtonClicked(view: View){
        //将view强制转化为TextView
        val tv = view as TextView

        currentInputNumSB.append(tv.text)
        if (isNumStart){
            //当前输入的是一个新的数字 ，添加到数组中
            numsList.add(tv.text.toString().toInt())
            //更改状态 已经不是一个新数字的开始了
            isNumStart = false
        }else{
            //用当前的数字去替换数组中最后一个元素
            numsList[numsList.size-1] = currentInputNumSB.toString().toInt()
        }


        //显示内容
        showUI()
        //计算结果
        calculate()
    }

    //运算符键
    fun operatorButtonClicked(view: View){
        //将view强制转化为TextView
        val tv = view as TextView
        //保存当前运算符
        operatorsList.add(tv.text.toString())
        //改变状态
        isNumStart = true
        currentInputNumSB.clear()

        //显示内容
        showUI()
    }

    //清空键
    private fun clearButtonClicked(view: View){
        process_textview.text = ""
        result_textview.text = "0"
        currentInputNumSB.clear()
        numsList.clear()
        operatorsList.clear()
        isNumStart = true
    }

    //数字键
    private fun backButtonClicked(view: View){
        //判断应该撤销运算符还是数字
        if(numsList.size > operatorsList.size){
            //撤销数字
            if (numsList.size > 0) {
                numsList.removeLast()
                isNumStart = true
                currentInputNumSB.clear()
            }
        }else{
            //撤销运算符
            if (operatorsList.size > 0) {
                operatorsList.removeLast()
                isNumStart = false
                if (numsList.size > 0) {
                    currentInputNumSB.append(numsList.last())
                }
            }
        }

        showUI()
        calculate()
    }

    //数字键
    fun equalButtonClicked(view: View){
        Log.v("myTag","equal")
    }

    //拼接当前运算的表达式 显示到界面上
    private fun showUI(){
        val str = StringBuilder()
        for ((i,num) in numsList.withIndex()){
            //将当前的数字拼接上去
            str.append(num)
            //判断运算符数组中对应位置是否有内容
            if(operatorsList.size > i){
                //将i对应的运算符拼接到字符串中 1 + 2
                str.append(" ${operatorsList[i]} ")
            }
        }
        process_textview.text = str.toString()
    }

    //实现逻辑运算功能
    private fun calculate(){
        if (numsList.size > 0) {
            //记录运算符数组遍历时的下标
            var i = 0
            //记录第一个运算数 == 数字数组的第一个数
            var param1 = numsList[0].toFloat()
            var param2 = 0.0f
            if (operatorsList.size > 0) {
                while (true) {
                    //获取i对应的运算符
                    var operator = operatorsList[i]
                    //判断是不是乘除
                    if (operator == "x" || operator == "÷") {
                        // 乘除直接运算
                        // 找到第二个运算数
                        if (i+1 < numsList.size) {
                            param2 = numsList[i + 1].toFloat()
                            // 运算
                            param1 = realCalculate(param1, operator, param2)
                        }
                    } else {
                        //判断是不是最后一个 或者 后面不是乘除
                        if (i == operatorsList.size-1 ||
                                (operatorsList[i+1] != "x" && operatorsList[i+1] != "÷")){
                            //可以直接运算
                            if (i < numsList.size-1) {
                                param2 = numsList[i + 1].toFloat()
                                param1 = realCalculate(param1, operator, param2)
                            }
                        }else{
                            //后面有而且是乘 或者 是除
                            var j = i+1
                            var mparam1 = numsList[j].toFloat()
                            var mparam2 = 0.0f
                            while (true){
                                //获取j对应的运算符
                                if (operatorsList[j] == "x" || operatorsList[j] == "÷"){
                                    if (j < operatorsList.size-1) {
                                        mparam2 = numsList[j + 1].toFloat()
                                        mparam1 = realCalculate(mparam1, operatorsList[j], mparam2)
                                    }
                                }else{
                                    //之前那个运算符后面所有连续的乘除都运算结束了
                                    break
                                }
                                j++
                                if (j == operatorsList.size){
                                    break
                                }
                            }
                            param2 = mparam1
                            param1 = realCalculate(param1, operator, param2)
                            i = j - 1
                        }

                    }
                    i++
                    if (i == operatorsList.size) {
                        //遍历结束了
                        break
                    }
                }
            }
            //显示对应的结果
            result_textview.text = String.format("%.1f",param1)
        }else{
            result_textview.text = "0"
        }
    }

    // y运算
    private fun realCalculate(
            param1: Float,
            operator: String,
            param2: Float):Float{
        var result:Float = 0.0f
        when (operator){
            "+" ->{
                result = param1 + param2
            }
            "-" ->{
                result = param1 - param2
            }
            "x" ->{
                result = param1 * param2
            }
            "÷" ->{
                result = param1 / param2
            }
        }
        return result
    }
}