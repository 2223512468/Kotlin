package com.xiaolei.okbook.Parsers

import com.xiaolei.okbook.Bean.IndexBook

object SearchParser
{
    
    fun parser(html: String, netName: String) = when (netName)
    {
        "潇湘书院" ->
        {
            xxsy(html, netName)
        }
        "起点中文网" ->
        {
            qdzww(html, netName)
        }
        else ->
        {
            IndexBook()
        }
    }
    
    /**
     * 潇湘书院
     */
    private fun xxsy(html: String, netName: String) = IndexParser.parser(html, netName)
    
    /**
     * 起点中文网
     */
    private fun qdzww(html: String, netName: String) = IndexParser.parser(html, netName)
}