import { Menu, Transition } from '@headlessui/react'
import { Fragment, useEffect, useRef, useState } from 'react'
import { ChevronDownIcon } from '@heroicons/react/20/solid'
import Logo from './Logo'
import { NavLink, useNavigate } from 'react-router-dom'

function Header() {

  const router = useNavigate()
  
  return (
    <div className='w-full bg-white flex flex-row items-center justify-between px-24 py-6'>
      <div className='font-extrabold text-black text-xl tracking-tighter flex flex-row items-start space-x-2'>
        <Logo />
        <div onClick={() => window.location.replace("/")} className='cursor-pointer flex flex-col items-start'>
          <span>Deal Dash</span>
          <span className='text-xs uppercase font-light tracking-wide'>Credit Card Analysis</span>
        </div>
      </div>
      <nav className='grid grid-cols-5 gap-4'>
        <NavLink onClick={() => window.location.replace('/')} to="/" className={({ isActive, isPending }) =>
            isPending ? "text-black text-center" : isActive ? "text-center text-green-700 font-semibold" : "text-center"
          }>
          Home
        </NavLink>
        <NavLink to="/best-deals" className={({ isActive, isPending }) =>
            isPending ? "text-black text-center" : isActive ? "text-center text-green-700 font-semibold" : "text-center"
          }>
          Best Deals
        </NavLink>
        <NavLink to="/support" className={({ isActive, isPending }) =>
            isPending ? "text-black text-center" : isActive ? "text-center text-green-700 font-semibold" : "text-center"
          }>
          Support
        </NavLink>
        <Menu as="div" className="ml-2 relative inline-block">
          <div>
            <Menu.Button className={`inline-flex w-full justify-center text-md rounded-md text-black focus:outline-none`}>
              Features
              <ChevronDownIcon
                className="-mr-1 ml-2 h-5 w-5 text-black"
                aria-hidden="true"
              />
            </Menu.Button>
          </div>
          <Transition
            as={Fragment}
            enter="transition ease-out duration-100"
            enterFrom="transform opacity-0 scale-95"
            enterTo="transform opacity-100 scale-100"
            leave="transition ease-in duration-75"
            leaveFrom="transform opacity-100 scale-100"
            leaveTo="transform opacity-0 scale-95"
          >
            <Menu.Items className="absolute left-0 mt-2 w-56 origin-top-right divide-y divide-gray-100 rounded-md bg-white shadow-lg ring-1 ring-black/5 focus:outline-none">
              <div className="px-1 py-1">
                <Menu.Item>
                  {({ active }) => (
                    <button
                      onClick={() => router('/')}
                      className={`${
                        active ? 'bg-green-500 text-white' : 'text-gray-900'
                      } group flex w-full items-center rounded-md px-2 py-2 text-sm`}
                    >
                      Word Completion
                    </button>
                  )}
                </Menu.Item>
                <Menu.Item>
                  {({ active }) => (
                    <button
                      onClick={() => router('/frequency-count')}
                      className={`${
                        active ? 'bg-green-500 text-white' : 'text-gray-900'
                      } group flex w-full items-center rounded-md px-2 py-2 text-sm`}
                    >
                      Frequency Count
                    </button>
                  )}
                </Menu.Item>
                <Menu.Item>
                  {({ active }) => (
                    <button
                      onClick={() => router('/?feature=spell-check')}
                      className={`${
                        active ? 'bg-green-500 text-white' : 'text-gray-900'
                      } group flex w-full items-center rounded-md px-2 py-2 text-sm`}
                    >
                      Spell Check
                    </button>
                  )}
                </Menu.Item>
                <Menu.Item>
                  {({ active }) => (
                    <button
                      onClick={() => router('/?feature=page-ranking')}
                      className={`${
                        active ? 'bg-green-500 text-white' : 'text-gray-900'
                      } group flex w-full items-center rounded-md px-2 py-2 text-sm`}
                    >
                      Page Ranking
                    </button>
                  )}
                </Menu.Item>
                <Menu.Item>
                  {({ active }) => (
                    <button
                      onClick={() => router('/?feature=inverted-indexing')}
                      className={`${
                        active ? 'bg-green-500 text-white' : 'text-gray-900'
                      } group flex w-full items-center rounded-md px-2 py-2 text-sm`}
                    >
                      Inverted Indexing
                    </button>
                  )}
                </Menu.Item>
                <Menu.Item>
                  {({ active }) => (
                    <button
                      onClick={() => router('/?feature=web-crawler')}
                      className={`${
                        active ? 'bg-green-500 text-white' : 'text-gray-900'
                      } group flex w-full items-center rounded-md px-2 py-2 text-sm`}
                    >
                      Web Crawler
                    </button>
                  )}
                </Menu.Item>
              </div>
            </Menu.Items>
          </Transition>
        </Menu>
        <NavLink to="/team" className={({ isActive, isPending }) =>
            isPending ? "text-black text-center" : isActive ? " text-center text-green-700 font-semibold" : "text-center"
          }>
          Team
        </NavLink>
      </nav>
    </div>
  )
}

export default Header