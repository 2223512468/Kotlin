@file:Suppress("DEPRECATION")

package com.xiaolei.okbook.Exts

import android.animation.Animator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Fragment
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Room
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.inputmethod.InputMethodManager
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import android.support.annotation.DrawableRes
import android.support.v7.widget.AppCompatSeekBar
import android.telephony.TelephonyManager
import android.widget.*
import android.view.*
import android.view.animation.Animation
import android.view.animation.Transformation
import com.xiaolei.okbook.Configs.Globals
import com.xiaolei.okbook.Database.AppDatabase
import com.xiaolei.okbook.R
import com.xiaolei.okbook.RetrofitExt.common.SCallBack
import com.xiaolei.okbook.Utils.DensityUtil
import com.xiaolei.okhttputil.Utils.Util
import com.xiaolei.smartpull2layout.SmartPullableLayout
import com.zhy.view.flowlayout.TagFlowLayout
import kotlinx.android.synthetic.main.activity_book_info.view.*
import retrofit2.Call
import kotlin.reflect.KClass


/**
 * Created by xiaolei on 2017/11/25.
 */
/**
 * Extension method to remove the required boilerplate for running code after a view has been
 * inflated and measured.
 *
 * @author Antonio Leiva
 * @see <a href="https://antonioleiva.com/kotlin-ongloballayoutlistener/>Kotlin recipes: OnGlobalLayoutListener</a>
 */

fun <T : View> T.hide()
{
    this.visibility = View.INVISIBLE
}

fun <T : View> T.show()
{
    this.visibility = View.VISIBLE
}

fun <T : View> T.gone()
{
    this.visibility = View.GONE
}

fun <T : View> T.toggleHide()
{
    this.visibility = if (this.visibility == View.VISIBLE)
    {
        View.INVISIBLE
    } else
    {
        View.VISIBLE
    }
}

/**
 * View 控件像抽屉一样，从顶部抽下来
 */
fun <T : View> T.expand(duration: Long? = null, afterAnim: (() -> Unit)? = null)
{
    val v = this
    v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    val targetHeight = v.measuredHeight
    v.layoutParams.height = 1
    v.visibility = View.VISIBLE
    val a = object : Animation()
    {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation)
        {
            v.layoutParams.height = if (interpolatedTime == 1f)
                ViewGroup.LayoutParams.WRAP_CONTENT
            else
                (targetHeight * interpolatedTime).toInt()
            v.requestLayout()
        }

        override fun willChangeBounds(): Boolean = true
    }
    // 1dp/ms
    a.duration = (targetHeight / v.context.resources.displayMetrics.density).toInt().toLong()

    duration?.let {
        a.duration = it
    }

    a.setAnimationListener(object : Animation.AnimationListener
    {
        override fun onAnimationRepeat(animation: Animation?) = Unit
        override fun onAnimationStart(animation: Animation?) = Unit
        override fun onAnimationEnd(animation: Animation?)
        {
            afterAnim?.invoke()
        }
    })
    v.startAnimation(a)
}

fun <T : View> T.collapse(duration: Long? = null, afterAnim: (() -> Unit)? = null)
{
    val v = this
    val initialHeight = v.measuredHeight
    val a = object : Animation()
    {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation)
        {
            if (interpolatedTime == 1f)
            {
                v.visibility = View.GONE
                v.layoutParams.height = initialHeight
                v.requestLayout()
            } else
            {
                v.layoutParams.height = (initialHeight * (1.0f - interpolatedTime)).toInt()
                v.requestLayout()
            }
        }

        override fun willChangeBounds(): Boolean = true
    }
    a.duration = ((initialHeight / v.context.resources.displayMetrics.density).toInt()).toLong()
    duration?.let {
        a.duration = it
    }

    a.setAnimationListener(object : Animation.AnimationListener
    {
        override fun onAnimationRepeat(animation: Animation?) = Unit
        override fun onAnimationStart(animation: Animation?) = Unit
        override fun onAnimationEnd(animation: Animation?)
        {
            afterAnim?.invoke()
        }
    })
    v.startAnimation(a)
}


/**
 * 动画开始
 */
fun <T : Animator> T.onAnimStart(onStart: () -> Unit)
{
    this.addListener(object : Animator.AnimatorListener
    {
        override fun onAnimationRepeat(animation: Animator?) = Unit
        override fun onAnimationEnd(animation: Animator?) = Unit
        override fun onAnimationCancel(animation: Animator?) = Unit
        override fun onAnimationStart(animation: Animator?)
        {
            onStart()
            removeListener(this)
        }
    })
}

/**
 * 动画结束
 */
fun <T : Animator> T.onAnimEnd(onEnd: () -> Unit)
{
    this.addListener(object : Animator.AnimatorListener
    {
        override fun onAnimationRepeat(animation: Animator?) = Unit
        override fun onAnimationEnd(animation: Animator?)
        {
            onEnd()
            removeListener(this)
        }

        override fun onAnimationCancel(animation: Animator?) = Unit
        override fun onAnimationStart(animation: Animator?) = Unit
    })
}

/**
 * 切换显示Viw
 */
fun <T : View> T.toggleGone()
{
    this.visibility = if (this.visibility == View.VISIBLE)
    {
        View.GONE
    } else
    {
        View.VISIBLE
    }
}

inline fun <T : View> T.afterMeasured(crossinline f: T.(width: Int, height: Int) -> Unit)
{
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener
    {
        override fun onGlobalLayout()
        {
            if (measuredWidth > 0 && measuredHeight > 0)
            {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                f(measuredWidth, measuredHeight)
            }
        }
    })
}

fun <T : View> T.getScreenWidth(): Int
{
    val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    return wm.defaultDisplay.width
}

fun <T : View> T.dp2px(dp: Int): Int = DensityUtil.dip2px(context, dp.toFloat())
fun <T : View> T.px2dp(px: Int): Int = DensityUtil.px2dip(context, px.toFloat())


/**
 * Extension method to simplify the code needed to apply spans on a specific sub string.
 */
inline fun SpannableStringBuilder.withSpan(vararg spans: Any, action: SpannableStringBuilder.() -> Unit):
        SpannableStringBuilder
{
    val from = length
    action()

    for (span in spans)
    {
        setSpan(span, from, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    return this
}

/**
 * Extension method to provide simpler access to {@link ContextCompat#getColor(int)}.
 */
fun Context.getColorCompat(color: Int) = ContextCompat.getColor(this, color)

/**
 * Extension method to provide simpler access to {@link View#getResources()#getString(int)}.
 */
fun View.getString(stringResId: Int): String = resources.getString(stringResId)

/**
 * Extension method to provide show keyboard for View.
 */
fun View.showKeyboard()
{
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    this.requestFocus()
    imm.showSoftInput(this, 0)
}

/**
 * Extension method to provide hide keyboard for [Activity].
 */
fun Activity.hideSoftKeyboard()
{
    if (currentFocus != null)
    {
        val inputMethodManager = getSystemService(Context
                .INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
    }
}

/**
 * Extension method to provide hide keyboard for [Fragment].
 */
fun Fragment.hideSoftKeyboard()
{
    activity?.hideSoftKeyboard()
}

/**
 * Extension method to provide hide keyboard for [View].
 */
fun View.hideKeyboard()
{
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

/**
 * Extension method to int time to 2 digit String
 */
fun Int.twoDigitTime() = if (this < 10) "0" + toString() else toString()

/**
 * Extension method to provide quicker access to the [LayoutInflater] from [Context].
 */
fun Context.getLayoutInflater() = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

/**
 * Extension method to provide quicker access to the [LayoutInflater] from a [View].
 */
fun View.getLayoutInflater() = context.getLayoutInflater()

/**
 * Extension method to replace all text inside an [Editable] with the specified [newValue].
 */
fun Editable.replaceAll(newValue: String)
{
    replace(0, length, newValue)
}

/**
 * Extension method to replace all text inside an [Editable] with the specified [newValue] while
 * ignoring any [android.text.InputFilter] set on the [Editable].
 */
fun Editable.replaceAllIgnoreFilters(newValue: String)
{
    val currentFilters = filters
    filters = emptyArray()
    replaceAll(newValue)
    filters = currentFilters
}

/**
 * Extension method to cast a char with a decimal value to an [Int].
 */
fun Char.decimalValue(): Int
{
    if (!isDigit())
        throw IllegalArgumentException("Out of range")
    return this.toInt() - '0'.toInt()
}


/**
 * Extension method to get Date for String with specified format.
 */
fun String.dateInFormat(format: String): Date?
{
    val dateFormat = SimpleDateFormat(format, Locale.US)
    var parsedDate: Date? = null
    try
    {
        parsedDate = dateFormat.parse(this)
    } catch (ignored: ParseException)
    {
        ignored.printStackTrace()
    }
    return parsedDate
}

/**
 * Extension method to get ClickableSpan.
 * e.g.
 * val loginLink = getClickableSpan(context.getColorCompat(R.color.colorAccent), { })
 */
fun getClickableSpan(color: Int, action: (view: View) -> Unit): ClickableSpan
{
    return object : ClickableSpan()
    {
        override fun onClick(view: View)
        {
            action
        }

        override fun updateDrawState(ds: TextPaint)
        {
            super.updateDrawState(ds)
            ds.color = color
        }
    }
}

fun Date.equs(date: Date): Boolean = (this.year == date.year && this.month == date.month && this.date == date.date)


fun RadioGroup.get(position: Int): RadioButton = this.getChildAt(position) as RadioButton

fun TextView.draw(@DrawableRes res: Int?, position: Position)
{
    val drawable = if (res != null) resources.getDrawable(res) else null
    this.draw(drawable, position)
}

fun TextView.draw(bitmap: Bitmap?, position: Position)
{
    val drawable = if (bitmap == null) null else BitmapDrawable(bitmap)
    this.draw(drawable, position)
}

fun TextView.draw(drawable: Drawable?, position: Position)
{
    drawable?.let {
        drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
    }
    when (position)
    {
        Position.LEFT ->
        {
            setCompoundDrawables(drawable, null, null, null)
        }
        Position.RIGHT ->
        {
            setCompoundDrawables(null, null, drawable, null)
        }
        Position.TOP ->
        {
            setCompoundDrawables(null, drawable, null, null)
        }
        Position.BOTTOM ->
        {
            setCompoundDrawables(null, null, null, drawable)
        }
    }
}

enum class Position
{
    LEFT, RIGHT, TOP, BOTTOM
}

fun <T> ListView.setOnItemClickListener(block: (T) -> Unit)
{
    this.setOnItemClickListener { _, _, position, _ ->
        val bean = this.adapter.getItem(position)
        block(bean as T)
    }
}

fun <T> Call<T>.enqueue(context: Context,
                        onSuccess: ((result: T) -> Unit),
                        onFinaly: (() -> Unit)? = null,
                        onFail: ((t: Throwable?) -> Unit)? = null)
{
    this.enqueue(object : SCallBack<T>(context)
    {
        override fun onSuccess(result: T)
        {
            onSuccess(result)
        }

        override fun onFinally()
        {
            onFinaly?.invoke()
        }

        override fun onFail(t: Throwable)
        {
            onFail?.let {
                onFail(t)
            }
            onFail ?: let {
                super.onFail(t)
            }
        }
    })
}

fun <T> Call<T>.enqueue(fragment: android.support.v4.app.Fragment,
                        onSuccess: ((result: T) -> Unit),
                        onFinaly: (() -> Unit)? = null,
                        onFail: ((t: Throwable?) -> Unit)? = null)
{
    this.enqueue(object : SCallBack<T>(fragment)
    {
        override fun onSuccess(result: T)
        {
            onSuccess(result)
        }

        override fun onFinally()
        {
            onFinaly?.invoke()
        }

        override fun onFail(t: Throwable)
        {
            onFail?.let {
                onFail(t)
            }
            onFail ?: let {
                super.onFail(t)
            }
        }
    })
}


fun String?.getInt(defValue: Int): Int
{
    return if (this == null)
    {
        defValue
    } else
    {
        this.toIntOrNull() ?: defValue
    }
}

fun SharedPreferences.edit(block: SharedPreferences.Editor.() -> Unit)
{
    val editor = this.edit()
    block.invoke(editor)
    editor.apply()
    editor.commit()
}

fun String.md5(): String = Util.encryptMD5(this)

/**
 * IMEI 号码
 */
@SuppressLint("MissingPermission")
fun Context.getImei(): String
{
    val imei = try
    {
        val telephonyManager = this.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        telephonyManager.deviceId
    } catch (e: Exception)
    {
        e.printStackTrace()
        ""
    }
    return imei
}

/**
 * 手机品牌
 */
fun Context.getPhoneBrand() = android.os.Build.BRAND

/**
 * 手机型号
 */
fun Context.getPhoneModel() = Build.MODEL

/**
 * 安卓版本
 */
fun Context.getAndroidVersion() = "${android.os.Build.VERSION.SDK_INT}"

/**
 * 将BitMap转换成图片
 */
fun ViewGroup.buildBitmap(): Bitmap?
{
    this.isDrawingCacheEnabled = true
    this.buildDrawingCache()
    return drawingCache
}

var TagFlowLayout.lastTagPosition: Int
    get()
    {
        return (this.getTag(R.id.tag_tmp_data) as? Int?) ?: 0
    }
    set(value)
    {
        this.setTag(R.id.tag_tmp_data, value)
    }

fun SmartPullableLayout.onPull(onPullUp: (() -> Unit)?, onPullDown: (() -> Unit)?)
{
    this.setOnPullListener(object : SmartPullableLayout.OnPullListener
    {
        override fun onPullUp()
        {
            onPullUp?.invoke()
        }

        override fun onPullDown()
        {
            onPullDown?.invoke()
        }
    })
}

fun AppCompatSeekBar.onSeek(block: (progress: Int) -> Unit)
{
    this.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener
    {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean)
        {
            block(progress)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
        override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
    })
}

fun Database(context: Context): AppDatabase = Room.databaseBuilder(context, AppDatabase::class.java, Globals.dbName).build()

fun Uri.openWithBrowser(context: Context)
{
    try
    {
        Toast.makeText(context, "调用外部浏览器打开", Toast.LENGTH_SHORT).show()
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.data = this
        context.startActivity(intent)
    } catch (e: Exception)
    {
        e.printStackTrace()
        Toast.makeText(context, "调用失败", Toast.LENGTH_SHORT).show()
    }
}

fun String.parseUri(): Uri = Uri.parse(this)

fun <T> LiveData<T>.observeNotNull(owner: LifecycleOwner, block: (T) -> Unit)
{
    this.observe(owner, android.arch.lifecycle.Observer {
        it?.let(block)
    })
}

fun View.resColor(@ColorRes id: Int) = context.resources.getColor(id)
