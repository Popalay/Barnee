package com.popalay.barnee.ui.screen.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BackdropScaffold
import androidx.compose.material.BackdropValue.Concealed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.rememberBackdropScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.popalay.barnee.data.model.Aggregation
import com.popalay.barnee.data.model.AggregationGroup
import com.popalay.barnee.ui.common.DrinkList
import com.popalay.barnee.ui.common.SimpleFlowRow
import com.popalay.barnee.ui.screen.navigation.LocalNavController
import com.popalay.barnee.ui.theme.BarneeTheme
import dev.chrisbanes.accompanist.insets.statusBarsPadding

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val navController: NavController = LocalNavController.current
    val viewModel: SearchViewModel = mavericksViewModel()
    val state by viewModel.collectAsState()
    val scaffoldState = rememberBackdropScaffoldState(Concealed)

    LaunchedEffect(state.isBackDropRevealed) {
        if (state.isBackDropRevealed) {
            scaffoldState.conceal()
        } else {
            scaffoldState.reveal()
        }
    }

    BackdropScaffold(
        scaffoldState = scaffoldState,
        appBar = {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .statusBarsPadding()
            ) {
                Text(
                    text = "Search",
                    color = MaterialTheme.colors.onSecondary,
                    style = MaterialTheme.typography.h1
                )
                Text(
                    text = "Apply",
                    color = MaterialTheme.colors.onSecondary,
                    modifier = modifier.clickable { viewModel.onApplyClicked() }
                )
            }
        },
        peekHeight = 80.dp,
        backLayerContent = {
            state.aggregation()?.let { aggregation ->
                Filters(
                    aggregation = aggregation,
                    selected = state.selectedGroups,
                    onFilterClicked = { viewModel.onFilterClicked(it) }
                )
            }
        },
        frontLayerContent = {
            Column(modifier = modifier.statusBarsPadding()) {
                OutlinedTextField(
                    value = state.searchQuery,
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        backgroundColor = MaterialTheme.colors.surface
                    ),
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
                DrinkList(
                    navController = navController,
                    drinks = state.drinks,
                    emptyMessage = "We currently have no drinks on your request",
                    contentPadding = contentPadding
                )
            }
        }
    )
}

@Composable
private fun Filters(
    aggregation: Aggregation,
    selected: Set<Pair<String, AggregationGroup>>,
    onFilterClicked: (Pair<String, AggregationGroup>) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .height(300.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        listOf(
            "Color" to aggregation.colored,
            "Taste" to aggregation.tasting,
            "Made with" to aggregation.withType,
            "Served in" to aggregation.servedIn,
            "Skill lvl" to aggregation.skill
        ).forEach { (title, group) ->
            AggregationGroup(
                name = title,
                group = group,
                selected = selected,
                onClick = onFilterClicked
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun AggregationGroup(
    name: String,
    group: AggregationGroup,
    selected: Set<Pair<String, AggregationGroup>>,
    onClick: (Pair<String, AggregationGroup>) -> Unit
) {
    val contentColor = LocalContentColor.current
    Text(
        text = name,
        color = MaterialTheme.colors.onSecondary,
        style = MaterialTheme.typography.h3
    )
    Spacer(modifier = Modifier.height(8.dp))
    SimpleFlowRow(
        mainAxisSpacing = 8.dp,
        crossAxisSpacing = 8.dp
    ) {
        group.values.forEach {
            val isSelected = it.key to group in selected
            Text(
                text = it.key.replace("-", " "),
                color = if (isSelected) contentColor else MaterialTheme.colors.onSecondary,
                modifier = Modifier
                    .background(if (isSelected) MaterialTheme.colors.onSecondary else Color.Transparent, RoundedCornerShape(40))
                    .border(1.dp, MaterialTheme.colors.onSecondary, RoundedCornerShape(40))
                    .clickable { onClick(it.key to group) }
                    .padding(vertical = 4.dp)
                    .padding(horizontal = 8.dp)
            )
        }
    }
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun SearchScreenLightPreview() {
    BarneeTheme {
        SearchScreen()
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun SearchScreenDarkPreview() {
    BarneeTheme(darkTheme = true) {
        SearchScreen()
    }
}