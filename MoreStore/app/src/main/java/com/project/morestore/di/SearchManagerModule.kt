package com.project.morestore.di

import android.content.Context
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.SearchManagerType
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class SearchManagerModule {

    @Provides
    fun getSearchManager(@ApplicationContext context: Context): SearchManager{
        SearchFactory.initialize(context);
        return SearchFactory.getInstance().createSearchManager(SearchManagerType.ONLINE)
    }
}