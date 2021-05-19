package com.huar.calendar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.huar.calendar.calendar.EventsCalendar
import com.huar.calendar.calendar.EventsCalendarUtil
import com.huar.calendar.databinding.ActivityMainBinding
import java.util.*


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var mViewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this //双向绑定，必须！
        mViewModel = ViewModelProvider.AndroidViewModelFactory(application).create(MainViewModel::class.java)

        binding.apply {
            viewModel = mViewModel
            click = ClickProxy()
        }

        val today = Calendar.getInstance()
        mViewModel.monthTitle.set(EventsCalendarUtil.getDateString(today, EventsCalendarUtil.YYYY_MM_ZH))

        val start = Calendar.getInstance()
        start.add(Calendar.YEAR, -10)

        val end = Calendar.getInstance()
        end.add(Calendar.YEAR, 10)

        binding.eventsCalendar.apply {
            //初始化
            setSelectionMode(binding.eventsCalendar.SINGLE_SELECTION)
                .setToday(today)
                .setMonthRange(start, end) //起始日历 前后10年
                .setWeekStartDay(Calendar.MONDAY, false) //起始日，周一
                .setCallback(object : EventsCalendar.Callback {
                    override fun onDaySelected(selectedDate: Calendar?) {
                        //单点回调calendar
                        println(
                            EventsCalendarUtil.getDateString(
                                selectedDate,
                                EventsCalendarUtil.YYYY_MM_DD
                            )
                        )
                    }

                    override fun onDayLongPressed(selectedDate: Calendar?) {
                        //长按，没啥用
                    }

                    override fun onMonthChanged(monthStartDate: Calendar?) {
                        //月份切换，日历头部和日历分开,在此回调更新
                        mViewModel.monthTitle.set(
                            EventsCalendarUtil.getDateString(
                                monthStartDate,
                                EventsCalendarUtil.YYYY_MM_ZH
                            )
                        )
                    }

                })
                .build()

            post {
                binding.eventsCalendar.setCurrentSelectedDate(today)
            }
        }

        /**
         *  标记测试
         */
        //字符串
        binding.eventsCalendar.addEvent("2021-05-07") //单个
        binding.eventsCalendar.addEvent(arrayOf("2021-05-08", "2021-05-09")) //多个

        //日历对象
        val c = Calendar.getInstance()
        c.add(Calendar.DAY_OF_MONTH, 2) //后天
        val c1 = Calendar.getInstance()
        c1.add(Calendar.DAY_OF_MONTH, 3) //大后天
        val c2 = EventsCalendarUtil.getAssignCalendar("2021-05-24",format = null) //指定日期的calendar
        binding.eventsCalendar.addEvent(c) //单个
        binding.eventsCalendar.addEvent(arrayOf(c1, c2))//多个

    }

    inner class ClickProxy {
        fun calendarPrevious() {
            binding.eventsCalendar.previousPage(true)
        }

        fun calendarNext() {
            binding.eventsCalendar.nextPage(true)
        }
    }
}