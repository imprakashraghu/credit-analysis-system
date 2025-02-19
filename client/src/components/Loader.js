import React from 'react'
import Logo from './Logo'
import Lottie from 'react-lottie-player'
import lottieJson from '../assets/loader.json'

function Loader() {
  return (
    <div className='w-full h-[calc(100vh_-_100px)] bg-white flex flex-col items-center justify-center'>
        <div className=' text-xl tracking-tighter flex flex-row items-start space-x-2'>
            {/* <Logo />
            <div className='flex flex-col items-start'>
            <span>Credit Card Analysis</span>
            <span className='text-xs uppercase font-light tracking-wide'>COMP 8457 - Final Project</span>
            </div> */}
            <div className='flex flex-col items-center justify-center'>
              <Lottie
                loop
                animationData={lottieJson}
                play
                style={{ width: 200, height: 200 }}
              />
              <span className='text-sm text-gray-600 py-2'>Getting things ready</span>
            </div>
        </div>
    </div>
  )
}

export default Loader