# Web Api server

### Configure planning bot 

Bot needs to be configurable. Client should be able to reschedule next planning.
User can change next planning date, day of the week and interval.

### Use cases

There are two use cases for configure planning-bot:

#### Changing interval day, day of week

* HTTP method: **POST**
* Method URL: `/settings`
* Accepted content types: `application/json`

Bot will save those setting without rescheduling next job and use new settings
to calculate next planning the next time. 
User can specify interval day, day of week or both at the same time.
 
| Field                  | Type           | Description  |
| -------------          |:-------------: | -----        |
| day_of_week            | integer        | scheduled planning day of the week, must be from 1 to 5 |
| interval_day           | integer        | scheduled interval day  |

Example 1:
* interval day e.g. 14 (two weeks)
* day of the week e.g. 4 (Thursday)

Example 2:
* interval day e.g. 7 (one week)

#### Changing next planning

* HTTP method: **POST**
* Method URL: `/date`
* Accepted content types: `application/json`

Bot will reschedule next job and save specified data.

| Field                        | Type           | Description  |
| -------------                |:-------------: | -----        |
| next_planning    (required)  | String         | date of next planning|

Example 1:
* next-planning e.g. 24-04-2021

#### Examples of requests body

1. Change day of the week to 5 (Friday) and interval day to 14 (two weeks interval).

Request body:

```
{
    "day_of_week": 5,
    "interval_day": 14
}
```

2. Change next planning date to 20-12-2020.

```
{
    "next-planning" : "20-12-2020"
}
```

#### Errors

#### Changing interval day, day of week
* error from `clojure.spec.alpha`:
    * field is not in proper type e.g. interval day is string
    * day of the week not in range (1-5) e.g. 7
    * interval not an integer number greater than 1.

#### Changing next planning
* error from `clojure.spec.alpha`:
    * field is not in proper type 
    * field not match given regex, date must be in format dd-MM-yyyy