import React from 'react'
import Header from './components/Header'
import {
  Route,
  Routes,
  BrowserRouter,
} from "react-router-dom"
import Home from './pages/Home'
import Team from './pages/Team'
import ScrapingView from './pages/ScrapingView'
import FrequencyCount from './pages/FrequencyCount'
import BestDeals from './pages/BestDeals'
import Support from './pages/Support'

function App() {
  return (
    <div>
        <BrowserRouter>
          <Header />
          <Routes>
            <Route element={<Home/>} path='/' />
            <Route element={<Team/>} path='/team' />
            <Route element={<ScrapingView />} path='/webscraping' />
            <Route element={<FrequencyCount />} path='/frequency-count' />
            <Route element={<BestDeals />} path='/best-deals' />
            <Route element={<Support />} path='/support' />
          </Routes>
        </BrowserRouter>
    </div>
  )
}

export default App