package com.example.jetpackcomposelearning

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.jetpackcomposelearning.ui.theme.JetpackComposeLearningTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }
            JetpackComposeLearningTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = { MainBottomNavigationBar(navController) }
                ) { paddingValues ->
                    Box(modifier = Modifier.padding(paddingValues)) {
                        NavHost(navController, startDestination = "home") {
                            composable("home") { MainScreen(isDarkTheme, { isDarkTheme = !isDarkTheme }) }
                            composable("cart") { CartScreen(isDarkTheme, { isDarkTheme = !isDarkTheme }) }
                            composable("profile") { ProfileScreen(isDarkTheme, { isDarkTheme = !isDarkTheme }) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainBottomNavigationBar(navController: NavHostController) {
    NavigationBar {
        NavigationBarItem(
            selected = navController.currentDestination?.route == "home",
            onClick = { navController.navigate("home") },
            label = { Text("Главная") },
            icon = { Icon(Icons.Default.Home, contentDescription = "Главная") }
        )
        NavigationBarItem(
            selected = navController.currentDestination?.route == "cart",
            onClick = { navController.navigate("cart") },
            label = { Text("Корзина") },
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Корзина") }
        )
        NavigationBarItem(
            selected = navController.currentDestination?.route == "profile",
            onClick = { navController.navigate("profile") },
            label = { Text("Профиль") },
            icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Профиль") }
        )
    }
}

@Composable
fun MainScreen(isDarkTheme: Boolean, onToggleTheme: () -> Unit) {
    val searchText = remember { mutableStateOf("") }
    val books = listOf(
        Book("Harry Potter", "harrypotter.jpg"),
        Book("Dandelion Wine", "winr.jpg"),
        Book("Arc De Triomphe", "arka.jpg")
    )
    val filteredBooks = books.filter { it.title.contains(searchText.value, ignoreCase = true) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SearchComponent(searchText)
        DarkModeToggleButton(onToggleTheme)

        val context = LocalContext.current
        Button(
            onClick = {
                val intent = Intent(context, AnimationActivity::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier.padding(10.dp)
        ) {
            Text("Запустить анимацию")
        }

        CardsComponent(filteredBooks)
        EmptyStateComponent(filteredBooks)
    }
}



@Composable
fun CartScreen(isDarkTheme: Boolean, onToggleTheme: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        DarkModeToggleButton(onToggleTheme)
        Text("Корзина", modifier = Modifier.padding(16.dp))
    }
}

@Composable
fun ProfileScreen(isDarkTheme: Boolean, onToggleTheme: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        DarkModeToggleButton(onToggleTheme)
        Text("Профиль", modifier = Modifier.padding(16.dp))
    }
}

data class Book(val title: String, val imageUrl: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchComponent(searchText: MutableState<String>) {
    SearchBar(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 10.dp),
        query = searchText.value,
        onQueryChange = { text -> searchText.value = text },
        onSearch = {},
        placeholder = { Text(text = "Поиск") },
        active = false,
        onActiveChange = {}
    ) {}
}

@Composable
fun DarkModeToggleButton(onClick: () -> Unit) {
    IconButton(onClick = onClick, modifier = Modifier.padding(10.dp)) {
        Icon(Icons.Default.DarkMode, contentDescription = "Переключить тему")
    }
}

@Composable
fun CardsComponent(books: List<Book>) {
    LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.padding(10.dp), contentPadding = PaddingValues(10.dp)) {
        items(books.size) { index ->
            BookCard(books[index])
        }
    }
}

@Composable
fun BookCard(book: Book) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        val imageRes = when (book.imageUrl) {
            "harrypotter.jpg" -> R.drawable.harrypotter
            "winr.jpg" -> R.drawable.winr
            "arka.jpg" -> R.drawable.arka
            else -> R.drawable.error
        }
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Обложка книги ${book.title}",
            modifier = Modifier.size(width = 140.dp, height = 220.dp).padding(bottom = 8.dp)
        )
        Text(text = book.title)
    }
}

@Composable
fun EmptyStateComponent(books: List<Book>) {
    if (books.isEmpty()) {
        Text(text = "Ничего не найдено", modifier = Modifier.padding(16.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    JetpackComposeLearningTheme {
        Column {
            SearchComponent(remember { mutableStateOf("") })
            DarkModeToggleButton {}
            MainScreen(isDarkTheme = false, onToggleTheme = {})
        }
    }
}
