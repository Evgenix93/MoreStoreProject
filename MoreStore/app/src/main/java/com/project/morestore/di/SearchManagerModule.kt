package com.project.morestore.di

import android.content.Context
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.SearchManagerType
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

//@Module
//@InstallIn(SingletonComponent::class)
/*abstract class SearchManagerModule {

    @Provides
    fun getSearchManager(context: Context): SearchManager{
        SearchFactory.initialize(context);
        return SearchFactory.getInstance().createSearchManager(SearchManagerType.ONLINE)
    }
}*/