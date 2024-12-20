   package com.example.alicorpdemo

import PisoViewModel
import PisoViewModelFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.alicorpdemo.database.AppDatabase
import com.example.alicorpdemo.login.LoginScreen
import com.example.alicorpdemo.login.SignupScreen
import com.example.alicorpdemo.login.UsuarioViewModel
import com.example.alicorpdemo.login.UsuarioViewModelFactory
import com.example.alicorpdemo.ui.theme.AlicorpDemoTheme

   class MainActivity : ComponentActivity() {
       private lateinit var pisoViewModel: PisoViewModel
       private lateinit var camaraViewModel: CamaraViewModel
       private lateinit var informeViewModel: InformeViewModel
       private lateinit var usuarioViewModel: UsuarioViewModel

       override fun onCreate(savedInstanceState: Bundle?) {
           super.onCreate(savedInstanceState)
           enableEdgeToEdge()

           val usuarioDao = AppDatabase.getDatabase(applicationContext).usuarioDao()
           usuarioViewModel = ViewModelProvider(this, UsuarioViewModelFactory(usuarioDao)).get(UsuarioViewModel::class.java)

           val pisoDao = AppDatabase.getDatabase(applicationContext).pisoDao()
           pisoViewModel = ViewModelProvider(this, PisoViewModelFactory(pisoDao)).get(PisoViewModel::class.java)

           val camaraDao = AppDatabase.getDatabase(applicationContext).camaraDao()
           camaraViewModel = ViewModelProvider(this, CamaraViewModelFactory(camaraDao)).get(CamaraViewModel::class.java)

           val informeDao = AppDatabase.getDatabase(applicationContext).informeDao()
           informeViewModel = ViewModelProvider(this, InformeViewModelFactory(informeDao)).get(InformeViewModel::class.java)

           setContent {
               AlicorpDemoTheme {
                   val context = LocalContext.current
                   val navController = rememberNavController()

                   NavHost(navController = navController, startDestination = "login") {
                       composable("login") {
                           LoginScreen(viewModel = usuarioViewModel, navController = navController,  context = context)
                       }

                       composable("signup") {
                           SignupScreen(viewModel = usuarioViewModel, navController = navController,  context = context)
                       }


                       composable("pisoList") {
                           PisoScreen(viewModel = pisoViewModel, navController = navController,  context = context)
                       }

                       composable("crearPiso") {
                           PisoCreateScreen(viewModel = pisoViewModel, navController = navController,  context = context)
                       }


                       composable(
                           "detalleCamaras/{pisoId}",
                           arguments = listOf(navArgument("pisoId") { type = NavType.IntType })
                       ) { backStackEntry ->
                           val pisoId = backStackEntry.arguments?.getInt("pisoId") ?: 0
                           CamaraScreen(pisoId = pisoId, viewModel = camaraViewModel, viewModelPiso = pisoViewModel, navController = navController, context = context)
                       }

                       composable(
                           "crearCamara/{pisoId}",
                           arguments = listOf(navArgument("pisoId") { type = NavType.IntType })
                       ) { backStackEntry ->
                           val pisoId = backStackEntry.arguments?.getInt("pisoId") ?: 0
                           CamaraCreateScreen(pisoId = pisoId, viewModel = camaraViewModel, viewModelPiso = pisoViewModel, navController = navController,  context = context)
                       }

                       composable(
                           "updateCamara/{camaraId}",
                           arguments = listOf(navArgument("camaraId") { type = NavType.IntType })
                       ) { backStackEntry ->
                           val camaraId = backStackEntry.arguments?.getInt("camaraId") ?: 0
                           CamaraUpdateScreen(camaraId = camaraId, viewModel = camaraViewModel, viewModelPiso = pisoViewModel, navController = navController,  context = context)
                       }


                       composable(
                           "detalleInformes/{camaraId}",
                           arguments = listOf(navArgument("camaraId") { type = NavType.IntType })
                       ) { backStackEntry ->
                           val camaraId = backStackEntry.arguments?.getInt("camaraId") ?: 0
                           InformeScreen(camaraId = camaraId, viewModel = informeViewModel, viewModelCamara = camaraViewModel, navController = navController)
                       }

                       composable(
                           "crearInforme/{camaraId}",
                           arguments = listOf(navArgument("camaraId") { type = NavType.IntType })
                       ) { backStackEntry ->
                           val camaraId = backStackEntry.arguments?.getInt("camaraId") ?: 0
                           InformeCreateScreen(camaraId = camaraId, viewModel = informeViewModel, viewModelCamara = camaraViewModel, navController = navController,  context = context)
                       }

                       composable(
                           "updateInforme/{informeId}",
                           arguments = listOf(navArgument("informeId") { type = NavType.IntType })
                       ) { backStackEntry ->
                           val informeId = backStackEntry.arguments?.getInt("informeId") ?: 0
                           InformeUpdateScreen(informeId = informeId, viewModel = informeViewModel, viewModelCamara = camaraViewModel, navController = navController,  context = context)
                       }


                   }


               }
           }
       }
   }

   @Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AlicorpDemoTheme {
        Greeting("Android")
    }
}