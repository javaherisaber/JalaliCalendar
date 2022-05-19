# JalaliCalendar

[![](https://jitpack.io/v/javaherisaber/JalaliCalendar.svg)](https://jitpack.io/#javaherisaber/JalaliCalendar)

JalaliCalendar implementaion for Kotlin with utility classes and functions

## Dependency
Top level build.gradle
```groovy
allprojects {
   repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```

Module level build.gradle
```groovy
dependencies {
  implementation "com.github.javaherisaber:JalaliCalendar:$versions.jalaliCalendar"
}
```

## Usage
### JalaliCalendar
Set calendar fields
```kt
val calendar = JalaliCalendar(1398, MonthPersian.Dey, 28, 12, 45, 30)
calendar.add(Calendar.DAY_OF_MONTH, 1)
```

Check for leap year
```kt
JalaliCalendar.isLeapYear(1403)
```

Compare two calendars
```kt
val first = JalaliCalendar(1398, MonthPersian.Dey, 28)
val second = JalaliCalendar(1398, MonthPersian.Dey, 20)
assertTrue(first > second)
```

Calendar Ranges
```kt
val start = JalaliCalendar(1398, MonthPersian.Dey, 1)
val end = JalaliCalendar(1398, MonthPersian.Dey, 30)
var index = 1
for (date in start..end) {
	assertEquals(date.get(Calendar.DAY_OF_MONTH), index)
	index++
}
```

Format calendar
```kt
val calendar = JalaliCalendar(1398, MonthPersian.Dey, 30, 13, 45, 30)
JalaliDateFormat("WW dd M yyyy ساعت HH:ii:ss").format(calendar) // دوشنبه 30 دی 1398 ساعت 13:45:30
JalaliDateFormat("yyyy/m/d").format(calendar) // 1398/10/30
JalaliDateFormat("WW dd M yyyy").format(calendar) // دوشنبه 30 دی 1398
JalaliDateFormat("dd M yyyy").format(calendar) // 30 دی 1398
JalaliDateFormat("ww dd M yyyy ساعت HH:ii a").format(calendar) // 03 30 دی 1398 ساعت 13:45 ع
JalaliDateFormat("W dd M yy ساعت HH:ii A").format(calendar) // د 30 دی 98 ساعت 13:45 عصر
JalaliDateFormat("yyyy/mm/dd HH:ii:ss").format(calendar) // 1398/10/30 13:45:30
```

Parse calendar
```kt
JalaliDateFormat("yyyy/mm/dd HH:ii:ss").parse("1398/10/30 13:45:30")
```

### Clock
Set fields
```kt
var clock = Clock(10, 45, 0)
clock++ // add one second
clock-- // subtract one second
clock *= 1.2f // add 20 percent of one hour
val clock2 = Clock.of("14:37:00")
```

Format clock
```kt
val clock = Clock(13, 45, 0)
ClockFormat("HH:ii:ss").format(clock) // 13:45:00
ClockFormat("hh:ii:ss A").format(clock) // 01:45:00 عصر
ClockFormat("HH:ii:ss a").format(clock) // 01:45:00 ع
ClockFormat("h:ii A").format(clock) // 1:45 عصر
ClockFormat("HH:ii:ss10").format(clock) // 13:45
ClockFormat("HH:ii10").format(Clock(15, 0, 0)) // 15
ClockFormat("HH:ii10").format(Clock(15, 45, 0)) // 15:45
```

Parse clock
```kt
ClockFormat("HH:ii:ss").parse("13:45:00")
```

Compare two clocks
```kt
val first = Clock(10, 0, 0)
val second = Clock(23, 0, 0)
assertTrue(first < second)
```

### ClockPeriod
```kt
val first = ClockPeriod(Clock(10, 0, 0), Clock(12, 0, 0))
val second = ClockPeriod(Clock(11, 0, 0), Clock(14, 0, 0))
assertTrue(first.isOverlap(second)) // check for overlap
```
