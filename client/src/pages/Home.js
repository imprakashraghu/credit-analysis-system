import React, { useEffect, useState } from 'react'
import SearchBar from '../components/SearchBar'
import { useNavigate } from 'react-router-dom'
import useQuery from '../useQuery'
import SpellCheck from '../components/SpellCheck'
import axios from 'axios'
import Card from '../components/Card'

function Home() {

    const router = useNavigate()
    const query = useQuery()
    const [dataFromSearch, setDataFromSearch] = useState([])
    const [deals, setDeals] = useState([])

    const resetStates = () => {
        setDataFromSearch([])
    }

    useEffect(() => {
        resetStates();
        return () => {}
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [query.get('feature')])

    useEffect(() => {
        async function getBestDeals() {
            axios.post('http://localhost:8000/bestDeals', { top: 4, category: 'all' })
            .then(response => setDeals(response.data.best_deals||[]))
            .catch(err => console.log(err))
        }
        getBestDeals()
    }, [])


  return (
    <div className='w-full h-full bg-white flex flex-col items-center justify-start px-24 py-6 space-y-3'>
        <span className='capitalize bg-green-600 rounded-full font-medium px-2 text-sm text-white'>{query.get('feature')?.replace(/ /g,' ')||'Word Completion'}</span>
        <h1 className='w-full text-black text-5xl font-extrabold text-center tracking-tighter'>
            Unlock the Best <br/>Credit Card Deals in <span className='text-red-500'>Canada</span>
        </h1>
        {
            query.get('feature') === 'spell-check' ? (
                <SpellCheck />
            ) : (
                <SearchBar 
                    type={query.get('feature')}
                    onSearch={data => setDataFromSearch(data)}
                />
            )
        }
        <br/><br/>

        {(dataFromSearch==='not-found')&&(
            <div className='w-full max-w-xl flex flex-col items-center space-y-1'>
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" className="w-8 h-8 text-gray-500">
                <path strokeLinecap="round" strokeLinejoin="round" d="M12 9v3.75m-9.303 3.376c-.866 1.5.217 3.374 1.948 3.374h14.71c1.73 0 2.813-1.874 1.948-3.374L13.949 3.378c-.866-1.5-3.032-1.5-3.898 0L2.697 16.126ZM12 15.75h.007v.008H12v-.008Z" />
                </svg>
                <p className='w-full text-gray-600 text-center py-2'>No Results Found</p>
            </div>
        )}

        {
            (query.get('feature') === 'web-crawler' && dataFromSearch !== 'invalid' && dataFromSearch?.filter(i => typeof i === 'string'))?.length ? (
                    <div className='w-full max-w-xl mb-10 overflow-y-auto flex flex-col items-center space-y-2 max-h-[400px]'>
                        {
                            dataFromSearch?.map(item => (
                                <div key={item} className='w-full p-2 border shadow-sm pb-2 bg-gray-100 rounded-md flex flex-col items-start justify-between space-y-2'>
                                    <a target='__blank' href={item} className='w-full text-left hover:underline cursor-pointer text-green-600 line-clamp-1'>{item}</a>
                                </div>
                            ))
                        }
                    </div>
                ) : (query.get('feature') === 'inverted-indexing' || query.get('feature') === 'page-ranking') && dataFromSearch !== 'not-found' ? (
                    <div className='w-full max-w-xl mb-10 overflow-y-auto flex flex-col items-center space-y-2 max-h-[400px]'>
                        {
                            dataFromSearch?.map(item => (
                                <div key={item.link} className='w-full p-2 border-b pb-2 flex flex-col items-start justify-between space-y-2'>
                                    <div className='w-full flex flex-row items-center space-x-2'>
                                        {query.get('feature') === 'page-ranking' && (<span className='w-5 h-5 rounded-full bg-black text-white p-1 text-sm font-medium flex items-center justify-center'>{item.rank}</span>)}
                                        <a target='__blank' href={item.link} className='w-full text-left hover:underline cursor-pointer text-green-600 line-clamp-1'>{item.link}</a>
                                    </div>
                                    <div className='w-full flex flex-row items-center space-x-2 ml-7'>
                                        <span className='rounded-lg text-gray-600 text-sm flex items-center justify-center space-x-2'>
                                            <span>{item.frequency}</span>
                                            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" className="w-4 h-4 text-gray-600">
                                                <path fillRule="evenodd" d="M12 2.25c-5.385 0-9.75 4.365-9.75 9.75s4.365 9.75 9.75 9.75 9.75-4.365 9.75-9.75S17.385 2.25 12 2.25ZM12.75 6a.75.75 0 0 0-1.5 0v6c0 .414.336.75.75.75h4.5a.75.75 0 0 0 0-1.5h-3.75V6Z" clipRule="evenodd" />
                                            </svg>
                                            <span>occurrences</span>  
                                        </span>
                                    </div>
                                </div>
                            ))
                        }
                    </div>
                ) : (
                <div className='w-full grid grid-cols-5 gap-4'>
                    <div 
                        onClick={() => router('/webscraping?site=rbc&title=Royal Bank Of Canada')}
                        className='transform cursor-pointer hover:border-green-500 border-2 hover:-translate-y-1 duration-200 rounded-lg bg-black flex flex-col items-start px-3 py-3 space-y-2 shadow-md'>
                        <img
                            src='https://seeklogo.com/images/R/rbc-royal-bank-of-canada-logo-D0A1D244CF-seeklogo.com.png'
                            className='w-auto h-20 contain'
                            alt='Royal Bank Logo'
                        />
                        <h2 className='text-white w-full font-semibold text-lg text-left'>Royal Bank</h2>

                    </div>
                    <div 
                        onClick={() => router('/webscraping?site=cibc&title=CIBC')}
                        className='transform cursor-pointer hover:border-green-500 border-2 hover:-translate-y-1 duration-200 rounded-lg bg-black flex flex-col items-start justify-between px-3 py-3 space-y-2 shadow-md'>
                        <img
                            src="https://upload.wikimedia.org/wikipedia/en/thumb/4/48/CIBC_logo_2021.svg/1280px-CIBC_logo_2021.svg.png"
                            className='w-32 mt-6 contain'
                            alt='CIBC Logo'
                        />
                        <h2 className='text-white w-full font-semibold text-lg text-left'>CIBC</h2>

                    </div>
                    <div
                        onClick={() => router('/webscraping?site=td&title=TD Bank')} 
                        className='transform cursor-pointer hover:border-green-500 border-2 hover:-translate-y-1 duration-200 rounded-lg bg-black flex flex-col items-start justify-end px-3 py-3 space-y-2 shadow-md'>
                        <img
                            src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS7d1WI5jg-DNKYpdNbxJ_W2fYQCgekou406w&s"
                            className='h-20 contain rounded-md'
                            alt='TD Bank Logo'
                        />
                        <h2 className='text-white w-full font-semibold text-lg text-left'>TD Bank</h2>

                    </div>
                    <div 
                        onClick={() => router('/webscraping?site=scotia&title=Scotia Bank')}
                        className='transform cursor-pointer hover:border-green-500 border-2 hover:-translate-y-1 duration-200 rounded-lg bg-black flex flex-col items-start justify-end px-3 py-3 space-y-2 shadow-md'>
                        <img
                            src="https://www.fintechfutures.com/files/2023/09/scotiabank.png"
                            className='w-20 contain rounded-md'
                            alt='Scotia Bank Logo'
                        />
                        <h2 className='text-white w-full font-semibold text-lg text-left'>Scotia Bank</h2>

                    </div>
                     <div
                        onClick={() => router('/webscraping?site=nbc&title=National Bank')} 
                        className='transform cursor-pointer hover:border-green-500 border-2 hover:-translate-y-1 duration-200 rounded-lg bg-black flex flex-col items-start justify-end px-3 py-3 space-y-2 shadow-md'>
                        <img
                            src="https://upload.wikimedia.org/wikipedia/commons/thumb/3/3e/National_Bank_Of_Canada.svg/2560px-National_Bank_Of_Canada.svg.png"
                            className='h-20 contain rounded-md'
                            alt='National Bank Logo'
                        />
                        <h2 className='text-white w-full font-semibold text-lg text-left'>National Bank</h2>

                    </div>
                   
                    
                </div>
            )
        }
        <br/>
        <div className='w-full flex flex-col items-center justify-center space-y-6 min-h-[600px]'>
            <div className='w-full flex flex-col items-center'>
                <h2 className='w-full text-black font-extrabold tracking-tighter text-center py-2 text-4xl'>
                    See What's best for you!
                </h2>
                <p className='w-full text-center text-md text-gray-600'>
                    One stop solution for your financial freedom
                </p>
            </div>
            <div className='w-full grid grid-cols-4 gap-4'>
                {
                    deals?.map(deal => (
                        <Card
                            key={deal.title}
                            title={deal.title}
                            image_url={deal.image_url}
                            annual_fee={deal.annual_fee}
                            purchase_fee={deal.purchase_fee}
                            link={deal.link}
                            category={deal.category}
                        />
                    ))
                }
            </div>
            <button
                onClick={() => router('best-deals')}
                className='bg-green-500 group text-white rounded-md my-3 px-3 py-2 text-sm font-medium text-center hover:bg-green-600'
            >See More</button>
        </div>
        <p className='w-full text-center text-gray-500 text-sm py-4 border-t pt-3'>
            COMP - 8457 | Advanced Computing Concepts · Group 4 · Credit Card Analysis 
        </p>
    </div>
  )
}

export default Home