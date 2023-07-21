package com.dicoding.habitapp.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import com.dicoding.habitapp.R
import com.dicoding.habitapp.data.Habit
import com.dicoding.habitapp.data.HabitRepository
import com.dicoding.habitapp.utils.Event
import com.dicoding.habitapp.utils.HabitSortType
import kotlinx.coroutines.launch

class HabitListViewModel(private val habitRepository: HabitRepository) : ViewModel() {

    private val _filter = MutableLiveData<HabitSortType>()
    private val _deleteEvent = MutableLiveData<Event<Int>>()

    val habits: LiveData<PagedList<Habit>> = _filter.switchMap {
        habitRepository.getHabits(it)
    }

    val deleteEvent: LiveData<Event<Int>>
        get() = _deleteEvent

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    private val _undo = MutableLiveData<Event<Habit>>()
    val undo: LiveData<Event<Habit>> = _undo

    init {
        _filter.value = HabitSortType.START_TIME
    }

    fun filter(filterType: HabitSortType) {
        _filter.value = filterType
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            _undo.value = Event(habit)
            habitRepository.deleteHabit(habit)
            _deleteEvent.postValue(Event(R.string.habit_deleted))
        }
    }

    fun insert(habit: Habit) {
        habitRepository.insertHabit(habit)
    }
}